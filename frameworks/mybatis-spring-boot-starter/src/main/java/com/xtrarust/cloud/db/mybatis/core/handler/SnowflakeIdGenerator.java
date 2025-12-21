package com.xtrarust.cloud.db.mybatis.core.handler;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.xtrarust.cloud.id.util.SnowflakeIdUtil;

/**
 * 自定义雪花算法生成器
 */
public class SnowflakeIdGenerator implements IdentifierGenerator {

    @Override
    public Number nextId(Object entity) {
        return SnowflakeIdUtil.nextId();
    }
}
