package com.xtrarust.cloud.db.mybatis.core.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.xtrarust.cloud.db.mybatis.core.entity.BaseDO;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 审计字段自动填充 <a href="https://baomidou.com/pages/4c6bcf"></a>
 *
 * @author gova
 * @see BaseDO
 */
public class AuditFieldMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Object originalObject = metaObject.getOriginalObject();
        if (originalObject instanceof BaseDO) {
            this.strictInsertFill(metaObject, "createBy", this::getUserName, String.class);
            this.strictInsertFill(metaObject, "updateBy", this::getUserName, String.class);
            this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);
            this.strictInsertFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Object originalObject = metaObject.getOriginalObject();
        if (originalObject instanceof BaseDO) {
            this.strictUpdateFill(metaObject, "updateBy", this::getUserName, String.class);
            this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
        }
    }

    /**
     * 获取 spring security 当前的用户名
     * @return 当前用户名
     */
    private String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            return null;
        }
        if (authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return authentication.getName();
    }
}
