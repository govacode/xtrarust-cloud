package com.xtrarust.cloud.common.util.ip;

import cn.hutool.http.HtmlUtil;
import com.xtrarust.cloud.common.util.NetUtils;
import com.xtrarust.cloud.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 获取地址类
 *
 * @author Lion Li
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AddressUtils {

    // 未知IP
    public static final String UNKNOWN_IP = "XX XX";
    // 内网地址
    public static final String LOCAL_ADDRESS = "内网IP";

    public static String getRealAddressByIP(String ip) {
        // 处理空串并过滤HTML标签
        ip = HtmlUtil.cleanHtmlTag(StringUtils.blankToDefault(ip, ""));
        // 判断是否为IPv4
        if (NetUtils.isIPv4(ip)) {
            return resolveIPv4Region(ip);
        }
        // 判断是否为IPv6
        if (NetUtils.isIPv6(ip)) {
            return resolveIPv6Region(ip);
        }
        // 如果不是IPv4或IPv6，则返回未知IP
        return UNKNOWN_IP;
    }

    /**
     * 根据IPv4地址查询IP归属行政区域
     *
     * @param ip ipv4地址
     * @return 归属行政区域
     */
    private static String resolveIPv4Region(String ip) {
        // 内网不查询
        if (NetUtils.isInnerIP(ip)) {
            return LOCAL_ADDRESS;
        }
        return RegionUtils.getCityInfo(ip);
    }

    /**
     * 根据IPv6地址查询IP归属行政区域
     *
     * @param ip ipv6地址
     * @return 归属行政区域
     */
    private static String resolveIPv6Region(String ip) {
        // 内网不查询
        if (NetUtils.isInnerIPv6(ip)) {
            return LOCAL_ADDRESS;
        }
        return RegionUtils.getCityInfo(ip);
    }

}
