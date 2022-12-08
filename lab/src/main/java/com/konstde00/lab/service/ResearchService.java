package com.konstde00.lab.service;

import com.konstde00.commons.exceptions.NotValidException;
import com.konstde00.lab.domain.dto.request.CreateResearchRequestDto;
import com.konstde00.lab.domain.dto.request.UpdateResearchRequestDto;
import com.konstde00.lab.domain.dto.response.ResearchResponseDto;
import com.konstde00.lab.domain.entity.Research;
import com.konstde00.lab.mapper.ResearchMapper;
import com.konstde00.lab.repository.ResearchRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@DependsOn("dataSourceRouting")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResearchService {

    ResearchRepository researchRepository;

    public Research findById(Long id) {

        return researchRepository.findById(id)
               .orElseThrow(() -> new NotValidException("Can't find research by id " + id));
    }

    public List<ResearchResponseDto> findAll() {

        List<Research> researches = researchRepository.findAll();

        return ResearchMapper.INSTANCE.toResponseDtoList(researches);
    }

    public ResearchResponseDto create(CreateResearchRequestDto requestDto) {

        Research research = ResearchMapper.INSTANCE.fromRequestDto(requestDto);

        research = researchRepository.saveAndFlush(research);

        return ResearchMapper.INSTANCE.toResponseDto(research);
    }

    public ResearchResponseDto update(UpdateResearchRequestDto dto) {

        Research research = findById(dto.getId());

        if (dto.getName() != null) research.setName(dto.getName());
        if (dto.getDescription() != null) research.setDescription(dto.getDescription());

        research = researchRepository.saveAndFlush(research);

        return ResearchMapper.INSTANCE.toResponseDto(research);
    }

    @Transactional
    public void delete(Long id) {

        researchRepository.deleteById(id);
    }
}
