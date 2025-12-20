package com.xtrarust.cloud.auth.repository.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xtrarust.cloud.auth.domain.entity.Oauth2AuthorizationConsentDO;
import com.xtrarust.cloud.db.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface Oauth2AuthorizationConsentMapper extends BaseMapperX<Oauth2AuthorizationConsentDO> {

    default Oauth2AuthorizationConsentDO selectByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName) {
        return selectOne(
                Oauth2AuthorizationConsentDO::getRegisteredClientId, registeredClientId,
                Oauth2AuthorizationConsentDO::getPrincipalName, principalName);
    }

    default void deleteByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName) {
        LambdaQueryWrapper<Oauth2AuthorizationConsentDO> queryWrapper =
                Wrappers.lambdaQuery(Oauth2AuthorizationConsentDO.class)
                        .eq(Oauth2AuthorizationConsentDO::getRegisteredClientId, registeredClientId)
                        .eq(Oauth2AuthorizationConsentDO::getPrincipalName, principalName);
        delete(queryWrapper);
    }

    default void updateByRegisteredClientIdAndPrincipalName(Oauth2AuthorizationConsentDO entity) {
        LambdaQueryWrapper<Oauth2AuthorizationConsentDO> queryWrapper =
                Wrappers.lambdaQuery(Oauth2AuthorizationConsentDO.class)
                        .eq(Oauth2AuthorizationConsentDO::getRegisteredClientId, entity.getRegisteredClientId())
                        .eq(Oauth2AuthorizationConsentDO::getPrincipalName, entity.getPrincipalName());
        update(entity, queryWrapper);
    }

}
