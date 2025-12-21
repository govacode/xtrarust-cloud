package com.xtrarust.cloud.db.mybatis.encrypt.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.xtrarust.cloud.common.util.StringUtils;
import com.xtrarust.cloud.db.mybatis.encrypt.annotation.EncryptField;
import com.xtrarust.cloud.db.mybatis.encrypt.core.encryptor.AbstractAsymmetricKeyEncryptor;
import com.xtrarust.cloud.db.mybatis.encrypt.core.encryptor.AbstractSymmetricKeyEncryptor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 加密管理类
 *
 * @author gova
 */
@Slf4j
@NoArgsConstructor
public class EncryptorManager {

    /**
     * 缓存加密器
     */
    private final Map<EncryptContext, Encryptor> encryptorMap = new ConcurrentHashMap<>();

    /**
     * 类加密字段缓存
     */
    private final Map<Class<?>, Set<Field>> fieldCache = new ConcurrentHashMap<>();

    /**
     * 构造方法传入类加密字段缓存
     *
     * @param typeAliasesPackage 实体类包
     */
    public EncryptorManager(String typeAliasesPackage) {
        scanEncryptClasses(typeAliasesPackage);
    }


    /**
     * 获取类加密字段缓存
     */
    public Set<Field> getFieldCache(Class<?> sourceClazz) {
        if (ObjectUtil.isNotNull(fieldCache)) {
            return fieldCache.get(sourceClazz);
        }
        return null;
    }

    /**
     * 注册加密执行者到缓存
     *
     * @param context 加密执行者需要的相关配置参数
     */
    public Encryptor registAndGetEncryptor(EncryptContext context) {
        if (encryptorMap.containsKey(context)) {
            return encryptorMap.get(context);
        }
        Class<? extends Encryptor> clazz = context.getAlgorithm().getClazz();
        Encryptor encryptor;
        if (AbstractSymmetricKeyEncryptor.class.isAssignableFrom(clazz)) {
            encryptor = ReflectUtil.newInstance(clazz, context.getKey());
        } else if (AbstractAsymmetricKeyEncryptor.class.isAssignableFrom(clazz)) {
            encryptor = ReflectUtil.newInstance(clazz, context.getPublicKey(), context.getPrivateKey());
        } else {
            encryptor = ReflectUtil.newInstance(clazz);
        }
        encryptorMap.put(context, encryptor);
        return encryptor;
    }

    /**
     * 移除缓存中的加密执行者
     *
     * @param context 加密执行者需要的相关配置参数
     */
    public void removeEncryptor(EncryptContext context) {
        this.encryptorMap.remove(context);
    }

    /**
     * 根据配置进行加密。会进行本地缓存对应的算法和对应的秘钥信息。
     *
     * @param value          待加密的值
     * @param context 加密相关的配置信息
     */
    public String encrypt(String value, EncryptContext context) {
        Encryptor encryptor = this.registAndGetEncryptor(context);
        return encryptor.encrypt(value, context.getEncode());
    }

    /**
     * 根据配置进行解密
     *
     * @param value          待解密的值
     * @param context 加密相关的配置信息
     */
    public String decrypt(String value, EncryptContext context) {
        Encryptor encryptor = this.registAndGetEncryptor(context);
        return encryptor.decrypt(value);
    }

    /**
     * 通过 typeAliasesPackage 设置的扫描包 扫描缓存实体
     */
    private void scanEncryptClasses(String typeAliasesPackage) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();
        String[] packagePatternArray = StringUtils.splitPreserveAllTokens(typeAliasesPackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
        try {
            for (String packagePattern : packagePatternArray) {
                String path = ClassUtils.convertClassNameToResourcePath(packagePattern);
                Resource[] resources = resolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + path + "/*.class");
                for (Resource resource : resources) {
                    ClassMetadata classMetadata = factory.getMetadataReader(resource).getClassMetadata();
                    Class<?> clazz = Resources.classForName(classMetadata.getClassName());
                    Set<Field> encryptFieldSet = getEncryptFieldSetFromClazz(clazz);
                    if (CollUtil.isNotEmpty(encryptFieldSet)) {
                        fieldCache.put(clazz, encryptFieldSet);
                    }
                }
            }
        } catch (Exception e) {
            log.error("初始化数据安全缓存时出错:{}", e.getMessage());
        }
    }

    /**
     * 获得一个类的加密字段集合
     */
    private Set<Field> getEncryptFieldSetFromClazz(Class<?> clazz) {
        Set<Field> fieldSet = new LinkedHashSet<>();
        // 判断clazz如果是接口,内部类,匿名类就直接返回
        if (clazz.isInterface() || clazz.isMemberClass() || clazz.isAnonymousClass()) {
            return fieldSet;
        }
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            fieldSet.addAll(Arrays.asList(fields));
            clazz = clazz.getSuperclass();
        }
        fieldSet = fieldSet.stream()
                .filter(field -> field.isAnnotationPresent(EncryptField.class) && field.getType() == String.class)
                .collect(Collectors.toSet());
        for (Field field : fieldSet) {
            field.setAccessible(true);
        }
        return fieldSet;
    }

}
