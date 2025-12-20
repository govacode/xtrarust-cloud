package com.xtrarust.cloud.auth.security.oauth2.client.userinfo.wechat;

/**
 * 微信用户信息字段名
 * <a href="https://developers.weixin.qq.com/doc/oplatform/Website_App/WeChat_Login/Authorized_Interface_Calling_UnionID.html"></a>
 *
 * @author gova
 */
public interface WechatUserInfoAttributes {

    /** 普通用户的标识，对当前开发者帐号唯一 */
    String OPENID = "openid";

    /** 普通用户昵称 */
    String NICKNAME = "nickname";

    /** 普通用户性别，1为男性，2为女性 */
    String SEX = "sex";

    /** 普通用户个人资料填写的省份 */
    String PROVINCE = "province";

    /** 普通用户个人资料填写的城市 */
    String CITY = "city";

    /** 国家，如中国为CN */
    String COUNTRY = "country";

    /** 用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空 */
    String HEAD_IMG_URL = "headimgurl";

    /** 用户特权信息，json数组，如微信沃卡用户为（chinaunicom） */
    String PRIVILEGE = "privilege";

    /** 用户统一标识。针对一个微信开放平台帐号下的应用，同一用户的unionid是唯一的。 */
    String UNION_ID = "unionid";
    
}
