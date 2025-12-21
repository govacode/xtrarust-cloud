package com.xtrarust.cloud.db.mybatis.encrypt.interceptor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.xtrarust.cloud.common.util.StringUtils;
import com.xtrarust.cloud.db.mybatis.encrypt.annotation.EncryptField;
import com.xtrarust.cloud.db.mybatis.encrypt.core.EncryptContext;
import com.xtrarust.cloud.db.mybatis.encrypt.core.EncryptorManager;
import com.xtrarust.cloud.db.mybatis.encrypt.enums.AlgorithmType;
import com.xtrarust.cloud.db.mybatis.encrypt.enums.EncodeType;
import com.xtrarust.cloud.db.mybatis.encrypt.properties.EncryptorProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.*;

/**
 * 出参解密拦截器
 *
 * @author gova
 */
@Slf4j
@Intercepts({@Signature(
        type = ResultSetHandler.class,
        method = "handleResultSets",
        args = {Statement.class})
})
@AllArgsConstructor
public class MybatisDecryptInterceptor implements Interceptor {

    private final EncryptorManager encryptorManager;

    private final EncryptorProperties defaultProperties;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取执行mysql执行结果
        Object result = invocation.proceed();
        if (result == null) {
            return null;
        }
        decryptHandler(result);
        return result;
    }

    private void decryptHandler(Object sourceObject) {
        if (ObjectUtil.isNull(sourceObject)) {
            return;
        }
        if (sourceObject instanceof Map<?, ?> map) {
            new HashSet<>(map.values()).forEach(this::decryptHandler);
            return;
        }
        if (sourceObject instanceof List<?> list) {
            if (CollUtil.isEmpty(list)) {
                return;
            }
            // 判断第一个元素是否含有注解。如果没有直接返回，提高效率
            Object firstItem = list.get(0);
            if (ObjectUtil.isNull(firstItem) || CollUtil.isEmpty(encryptorManager.getFieldCache(firstItem.getClass()))) {
                return;
            }
            list.forEach(this::decryptHandler);
            return;
        }
        // 不在缓存中的类,就是没有加密注解的类(当然也有可能是typeAliasesPackage写错)
        Set<Field> fields = encryptorManager.getFieldCache(sourceObject.getClass());
        if (ObjectUtil.isNull(fields)) {
            return;
        }
        try {
            for (Field field : fields) {
                field.set(sourceObject, this.decryptField(Convert.toStr(field.get(sourceObject)), field));
            }
        } catch (Exception e) {
            log.error("处理解密字段时出错", e);
        }
    }

    /**
     * 字段值进行加密。通过字段的批注注册新的加密算法
     *
     * @param value 待加密的值
     * @param field 待加密字段
     * @return 加密后结果
     */
    private String decryptField(String value, Field field) {
        if (ObjectUtil.isNull(value)) {
            return null;
        }
        EncryptField encryptField = field.getAnnotation(EncryptField.class);
        EncryptContext context = new EncryptContext();
        context.setAlgorithm(encryptField.algorithm() == AlgorithmType.DEFAULT ? defaultProperties.getAlgorithm() : encryptField.algorithm());
        context.setEncode(encryptField.encode() == EncodeType.DEFAULT ? defaultProperties.getEncode() : encryptField.encode());
        context.setKey(StringUtils.isBlank(encryptField.key()) ? defaultProperties.getKey() : encryptField.key());
        context.setPrivateKey(StringUtils.isBlank(encryptField.privateKey()) ? defaultProperties.getPrivateKey() : encryptField.privateKey());
        context.setPublicKey(StringUtils.isBlank(encryptField.publicKey()) ? defaultProperties.getPublicKey() : encryptField.publicKey());
        return this.encryptorManager.decrypt(value, context);
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
