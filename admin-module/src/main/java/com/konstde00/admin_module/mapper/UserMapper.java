package com.konstde00.admin_module.mapper;

import com.konstde00.admin_module.domain.dto.request.CreateUserRequestDto;
import com.konstde00.commons.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User fromRequestDto(CreateUserRequestDto requestDto);
}
