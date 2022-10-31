package com.konstde00.admin_module.mapper;

import com.konstde00.admin_module.domain.dto.request.CreateTenantRequestDto;
import com.konstde00.admin_module.domain.dto.response.TenantResponseDto;
import com.konstde00.commons.domain.entity.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TenantMapper {

    TenantMapper INSTANCE = Mappers.getMapper(TenantMapper.class);

    Tenant fromRequestDto(CreateTenantRequestDto requestDto);

    TenantResponseDto toResponseDto(Tenant tenant);

    List<TenantResponseDto> toResponseDtoList(List<Tenant> tenants);
}
