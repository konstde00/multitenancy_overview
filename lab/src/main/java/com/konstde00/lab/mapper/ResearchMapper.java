package com.konstde00.lab.mapper;

import com.konstde00.lab.domain.dto.request.CreateResearchRequestDto;
import com.konstde00.lab.domain.dto.response.ResearchResponseDto;
import com.konstde00.lab.domain.entity.Research;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ResearchMapper {

    ResearchMapper INSTANCE = Mappers.getMapper(ResearchMapper.class);

    Research fromRequestDto(CreateResearchRequestDto requestDto);

    ResearchResponseDto toResponseDto(Research tenant);

    List<ResearchResponseDto> toResponseDtoList(List<Research> tenants);
}
