package com.xtrarust.cloud.auth.repository.mapper;

import com.xtrarust.cloud.auth.domain.entity.Oauth2UserDO;
import com.xtrarust.cloud.db.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface Oauth2UserMapper extends BaseMapperX<Oauth2UserDO> {

    default Oauth2UserDO selectByTypeAndUsername(String type, String username) {
        return selectOne(Oauth2UserDO::getType, type, Oauth2UserDO::getUsername, username);
    }
}
