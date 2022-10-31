package com.konstde00.admin_module.controller;

import com.konstde00.admin_module.domain.dto.request.CreateTenantRequestDto;
import com.konstde00.admin_module.domain.dto.request.RenameTenantRequestDto;
import com.konstde00.admin_module.domain.dto.response.TenantResponseDto;
import com.konstde00.admin_module.mapper.TenantMapper;
import com.konstde00.admin_module.service.TenantService;
import com.konstde00.commons.domain.entity.Tenant;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@AllArgsConstructor
@RequestMapping("/api/tenants")
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TenantController {

    TenantService tenantService;

    @GetMapping
    public ResponseEntity<List<TenantResponseDto>> getAllTenants() {

        List<Tenant> tenants = tenantService.findAll();

        List<TenantResponseDto> responseDtos
            = TenantMapper.INSTANCE.toResponseDtoList(tenants);

        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TenantResponseDto> createTenant(@RequestBody CreateTenantRequestDto tenantDto) {

        TenantResponseDto tenant = tenantService.create(tenantDto);

        return new ResponseEntity<>(tenant, HttpStatus.CREATED);
    }

    @PatchMapping
    public ResponseEntity<?> renameTenant(@RequestBody RenameTenantRequestDto params) {

        tenantService.rename(params);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTenant(@RequestParam @NonNull Long id) {

        tenantService.delete(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
