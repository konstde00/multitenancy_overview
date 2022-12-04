package com.konstde00.lab.controller;

import com.konstde00.lab.domain.dto.request.CreateResearchRequestDto;
import com.konstde00.lab.domain.dto.request.UpdateResearchRequestDto;
import com.konstde00.lab.domain.dto.response.ResearchResponseDto;
import com.konstde00.lab.service.ResearchService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@AllArgsConstructor
@RequestMapping("/api/researches")
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ResearchController {

    ResearchService researchService;

    @GetMapping("/v1")
    @Operation(summary = "Get all researches")
    public ResponseEntity<List<ResearchResponseDto>> getAll(HttpServletRequest request) {

        List<ResearchResponseDto> researches = researchService.findAll();

        return new ResponseEntity<>(researches, HttpStatus.OK);
    }

    @PostMapping("/v1")
    @Operation(summary = "Create a new research")
    public ResponseEntity<ResearchResponseDto> create(@RequestBody CreateResearchRequestDto researchDto,
                                                      HttpServletRequest request) {

        ResearchResponseDto research = researchService.create(researchDto);

        return new ResponseEntity<>(research, HttpStatus.CREATED);
    }

    @PatchMapping("/v1")
    @Operation(summary = "Update research")
    public ResponseEntity<ResearchResponseDto> update(@RequestBody UpdateResearchRequestDto params,
                                                      HttpServletRequest request) {

        ResearchResponseDto research = researchService.update(params);

        return new ResponseEntity<>(research, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/v1")
    @Operation(summary = "Delete research")
    public ResponseEntity<?> deleteById(@RequestParam @NonNull Long id,
                                        HttpServletRequest request) {

        researchService.delete(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
