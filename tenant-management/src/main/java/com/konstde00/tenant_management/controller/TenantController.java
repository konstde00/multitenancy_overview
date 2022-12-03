package com.konstde00.tenant_management.controller;

import com.konstde00.commons.domain.entity.Tenant;
import com.konstde00.tenant_management.domain.dto.request.CreateTenantRequestDto;
import com.konstde00.tenant_management.domain.dto.request.RenameTenantRequestDto;
import com.konstde00.tenant_management.domain.dto.response.TenantResponseDto;
import com.konstde00.tenant_management.mapper.TenantMapper;
import com.konstde00.tenant_management.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
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

    @GetMapping("/v1")
    @Operation(summary = "Get all tenants")
    public ResponseEntity<List<TenantResponseDto>> getAllTenants() {

        List<Tenant> tenants = tenantService.findAll();

        List<TenantResponseDto> responseDtos
            = TenantMapper.INSTANCE.toResponseDtoList(tenants);

        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    @PostMapping("/v1")
    @Operation(summary = "Create a new tenant")
    public ResponseEntity<TenantResponseDto> createTenant(@RequestBody CreateTenantRequestDto tenantDto) {

        TenantResponseDto tenant = tenantService.create(tenantDto);

        return new ResponseEntity<>(tenant, HttpStatus.CREATED);
    }

    @PatchMapping("/v1")
    @Operation(summary = "Rename tenant")
    public ResponseEntity<?> renameTenant(@RequestBody RenameTenantRequestDto params) {

        tenantService.rename(params);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/v1")
    @Operation(summary = "Delete tenant")
    public ResponseEntity<?> deleteTenant(@RequestParam @NonNull Long id) {

        tenantService.delete(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
