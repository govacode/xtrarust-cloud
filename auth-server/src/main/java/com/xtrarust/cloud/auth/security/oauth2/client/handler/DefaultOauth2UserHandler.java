package com.xtrarust.cloud.auth.security.oauth2.client.handler;

import com.xtrarust.cloud.auth.domain.entity.Oauth2UserDO;
import com.xtrarust.cloud.auth.repository.mapper.Oauth2UserMapper;
import com.xtrarust.cloud.auth.security.oauth2.client.Oauth2ClientConstants;
import com.xtrarust.cloud.auth.security.oauth2.client.userinfo.qq.QQUserInfo;
import com.xtrarust.cloud.auth.security.oauth2.client.userinfo.wechat.WechatUserInfoAttributes;
import com.xtrarust.cloud.common.util.json.JacksonUtil;
import jakarta.annotation.Resource;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.BiConsumer;

@Component
public class DefaultOauth2UserHandler implements BiConsumer<String, OAuth2User> {

    @Resource
    private Oauth2UserMapper oauth2UserMapper;

    @Override
    public void accept(String clientRegistrationId, OAuth2User oAuth2User) {
        Oauth2UserDO existingUser = oauth2UserMapper.selectByTypeAndUsername(clientRegistrationId, oAuth2User.getName());
        if (existingUser != null) {
            Oauth2UserDO entity = toEntity(clientRegistrationId, oAuth2User);
            entity.setId(existingUser.getId());
            oauth2UserMapper.updateById(entity);
        } else {
            oauth2UserMapper.insert(toEntity(clientRegistrationId, oAuth2User));
        }
    }

    private static Oauth2UserDO toEntity(String clientRegistrationId, OAuth2User oAuth2User) {
        Oauth2UserDO entity = new Oauth2UserDO();
        entity.setType(clientRegistrationId);
        if (oAuth2User instanceof QQUserInfo qqUserInfo) {
            entity.setOpenid(qqUserInfo.getOpenid());
            entity.setUsername(qqUserInfo.getName());
            entity.setAvatar(qqUserInfo.getFigureUrlQQ100());
            entity.setRawUserInfo(JacksonUtil.toJsonString(qqUserInfo));
        } else if (oAuth2User instanceof DefaultOAuth2User defaultOAuth2User) {
            Map<String, Object> attributes = defaultOAuth2User.getAttributes();
            entity.setUsername(oAuth2User.getName());
            if (Oauth2ClientConstants.REGISTRATION_ID_WECHAT.equals(clientRegistrationId)) {
                entity.setOpenid((String) attributes.get(WechatUserInfoAttributes.OPENID));
                entity.setAvatar((String) attributes.get(WechatUserInfoAttributes.HEAD_IMG_URL));
                entity.setLocation((String) attributes.get(WechatUserInfoAttributes.COUNTRY));
                entity.setRawUserInfo(JacksonUtil.toJsonString(attributes));
            }
        }
        return entity;
    }
}
