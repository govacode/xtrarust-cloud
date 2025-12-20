package com.xtrarust.cloud.auth.repository.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xtrarust.cloud.auth.domain.entity.Oauth2AuthorizationDO;
import com.xtrarust.cloud.db.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface Oauth2AuthorizationMapper extends BaseMapperX<Oauth2AuthorizationDO> {

    default Oauth2AuthorizationDO selectByState(String state) {
        return selectOne(Oauth2AuthorizationDO::getState, state);
    }

    default Oauth2AuthorizationDO selectByAuthorizationCode(String authorizationCode) {
        return selectOne(Oauth2AuthorizationDO::getAuthorizationCodeValue, authorizationCode);
    }

    default Oauth2AuthorizationDO selectByAccessToken(String accessToken) {
        return selectOne(Oauth2AuthorizationDO::getAccessTokenValue, accessToken);
    }

    default Oauth2AuthorizationDO selectByRefreshToken(String refreshToken) {
        return selectOne(Oauth2AuthorizationDO::getRefreshTokenValue, refreshToken);
    }

    default Oauth2AuthorizationDO selectByIdToken(String idToken) {
        return selectOne(Oauth2AuthorizationDO::getOidcIdTokenValue, idToken);
    }

    default Oauth2AuthorizationDO selectByUserCode(String userCode) {
        return selectOne(Oauth2AuthorizationDO::getUserCodeValue, userCode);
    }

    default Oauth2AuthorizationDO selectByDeviceCode(String deviceCode) {
        return selectOne(Oauth2AuthorizationDO::getDeviceCodeValue, deviceCode);
    }

    default Oauth2AuthorizationDO selectByToken(String token) {
        LambdaQueryWrapper<Oauth2AuthorizationDO> queryWrapper = new LambdaQueryWrapper<Oauth2AuthorizationDO>()
                .eq(Oauth2AuthorizationDO::getState, token).or()
                .eq(Oauth2AuthorizationDO::getAuthorizationCodeValue, token).or()
                .eq(Oauth2AuthorizationDO::getAccessTokenValue, token).or()
                .eq(Oauth2AuthorizationDO::getRefreshTokenValue, token).or()
                .eq(Oauth2AuthorizationDO::getOidcIdTokenValue, token).or()
                .eq(Oauth2AuthorizationDO::getUserCodeValue, token).or()
                .eq(Oauth2AuthorizationDO::getDeviceCodeValue, token);
        return selectOne(queryWrapper);
    }

}
