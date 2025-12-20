package com.xtrarust.cloud.auth.security.oauth2.client.userinfo.qq;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.*;

/**
 * QQ用户信息 <a href="https://wiki.open.qq.com/wiki/%E3%80%90QQ%E7%99%BB%E5%BD%95%E3%80%91get_user_info"></a>
 *
 * @author gova
 */
@Data
public class QQUserInfo implements OAuth2User {

    private String openid;

    /** 返回码 */
    private String ret;

    /** 如果ret<0，会有相应的错误信息提示 */
    private String msg;

    /** 用户在QQ空间的昵称 */
    private String nickname;

    /** 大小为30×30像素的QQ空间头像URL */
    @JsonProperty("figureurl")
    private String figureUrl30;

    /** 大小为50×50像素的QQ空间头像URL */
    @JsonProperty("figureurl_1")
    private String figureUrl50;

    /** 大小为100×100像素的QQ空间头像URL */
    @JsonProperty("figureurl_2")
    private String figureUrl100;

    /** 大小为40×40像素的QQ头像URL */
    @JsonProperty("figureurl_qq_1")
    private String figureUrlQQ40;

    /** 大小为100×100像素的QQ头像URL。需要注意，不是所有的用户都拥有QQ的100x100的头像，但40x40像素则是一定会有 */
    @JsonProperty("figureurl_qq_2")
    private String figureUrlQQ100;

    /** 性别。如果获取不到则默认返回"男" */
    private String gender;

    /** 标识用户是否为黄钻用户（0：不是；1：是） */
    @JsonProperty("is_yellow_vip")
    private String isYellowVip;

    /** 标识用户是否为黄钻用户（0：不是；1：是） */
    private String vip;

    /** 黄钻等级 */
    @JsonProperty("yellow_vip_level")
    private String yellowVipLevel;

    /** 黄钻等级 */
    private String level;

    /** 标识是否为年费黄钻用户（0：不是； 1：是） */
    @JsonProperty("is_yellow_year_vip")
    private String isYellowYearVip;

    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("openid", openid);
        attributes.put("nickname", nickname);
        attributes.put("figureUrl30", figureUrl30);
        attributes.put("figureUrl50", figureUrl50);
        attributes.put("figureUrl100", figureUrl100);
        attributes.put("figureUrlQQ40", figureUrlQQ40);
        attributes.put("figureUrlQQ100", figureUrlQQ100);
        attributes.put("gender", gender);
        attributes.put("isYellowVip", isYellowVip);
        attributes.put("yellowVipLevel", yellowVipLevel);
        attributes.put("isYellowYearVip", isYellowYearVip);
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        authorities.add(new OAuth2UserAuthority(getAttributes()));
        return authorities;
    }

    @Override
    public String getName() {
        return nickname;
    }
}
