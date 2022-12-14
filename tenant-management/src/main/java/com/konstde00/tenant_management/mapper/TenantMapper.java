package com.konstde00.tenant_management.mapper;

import com.konstde00.commons.domain.entity.Tenant;
import com.konstde00.tenant_management.domain.dto.request.CreateTenantRequestDto;
import com.konstde00.tenant_management.domain.dto.response.TenantResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TenantMapper {

    TenantMapper INSTANCE = Mappers.getMapper(TenantMapper.class);

    Tenant fromRequestDto(CreateTenantRequestDto requestDto);

    @Mapping(target = "userId", ignore = true)
    TenantResponseDto toResponseDto(Tenant tenant);

    List<TenantResponseDto> toResponseDtoList(List<Tenant> tenants);
}
