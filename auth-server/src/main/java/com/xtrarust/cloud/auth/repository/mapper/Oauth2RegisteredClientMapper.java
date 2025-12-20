package com.xtrarust.cloud.auth.repository.mapper;

import com.xtrarust.cloud.auth.domain.entity.Oauth2RegisteredClientDO;
import com.xtrarust.cloud.db.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface Oauth2RegisteredClientMapper extends BaseMapperX<Oauth2RegisteredClientDO> {

    default Oauth2RegisteredClientDO selectByClientId(String clientId) {
        return selectOne(Oauth2RegisteredClientDO::getClientId, clientId);
    }
}
