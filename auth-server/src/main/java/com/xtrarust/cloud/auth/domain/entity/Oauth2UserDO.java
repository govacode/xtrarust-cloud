package com.xtrarust.cloud.auth.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xtrarust.cloud.db.mybatis.core.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * OAuth2三方账户
 *
 * @author gova
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oauth2_user")
public class Oauth2UserDO extends BaseDO {

    @TableId
    private Long id;

    /**
     * 三方登录类型
     */
    private String type;

    /**
     * 三方登录唯一id
     */
    private String openid;

    /**
     * 三方登录用户名
     */
    private String username;

    /**
     * 三方登录用户头像
     */
    private String avatar;

    /**
     * 三方登录用户邮箱
     */
    private String email;

    /**
     * 三方登录用户博客地址
     */
    private String blog;

    /**
     * 三方登录用户地址
     */
    private String location;

    /**
     * 三方登录完整用户信息
     */
    private String rawUserInfo;
}
