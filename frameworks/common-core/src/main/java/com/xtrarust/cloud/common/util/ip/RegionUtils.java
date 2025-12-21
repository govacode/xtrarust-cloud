package com.xtrarust.cloud.common.util.ip;

import cn.hutool.core.io.resource.ClassPathResource;
import com.xtrarust.cloud.common.util.StringUtils;
import org.lionsoul.ip2region.service.Config;
import org.lionsoul.ip2region.service.Ip2Region;

/**
 * 根据ip地址定位工具类，离线方式
 * 参考地址：<a href="https://gitee.com/lionsoul/ip2region/tree/master/binding/java">集成 ip2region 实现离线IP地址定位库</a>
 *
 * @author gova
 */
//@Slf4j
public class RegionUtils {

    private static final Ip2Region ip2Region;

    static {
        try {
            // 1.创建 v4 的配置：指定缓存策略和 v4 的 xdb 文件路径
            final Config v4Config = Config.custom()
                    .setCachePolicy(Config.BufferCache)
                    .setSearchers(15)
                    .setXdbInputStream(new ClassPathResource("ip2region_v4.xdb").getStream())   // 设置 v4 xdb 文件的路径
                    .asV4();

            // 2.创建 v6 的配置：指定缓存策略和 v6 的 xdb 文件路径
            final Config v6Config = Config.custom()
                    .setCachePolicy(Config.BufferCache)
                    .setSearchers(15)
                    .setXdbInputStream(new ClassPathResource("ip2region_v6.xdb").getStream())
                    .asV6();
            // 3.通过上述配置创建 Ip2Region 查询服务
            ip2Region = Ip2Region.create(v4Config, v6Config);
        } catch (Exception e) {
            throw new RuntimeException("RegionUtils初始化失败，原因：" + e.getMessage());
        }
    }

    /**
     * 根据IP地址离线获取城市
     */
    public static String getCityInfo(String ip) {
        try {
            String region = ip2Region.search(StringUtils.trim(ip));
            return region.replace("0|", "").replace("|0", "");
        } catch (Exception e) {
//            log.error("IP地址离线获取城市异常 {}", ip);
            return "未知";
        }
    }

}
