package com.konstde00.tenant_management.mapper;

import com.konstde00.commons.domain.entity.User;
import com.konstde00.tenant_management.domain.dto.request.CreateUserRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User fromRequestDto(CreateUserRequestDto requestDto);
}
