package com.konstde00.admin_module.repository;

import com.konstde00.commons.domain.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    @Modifying
    @Query(value = """

    update tenants
    set name = ?2
    where id = ?1

    """, nativeQuery = true)
    void rename(Long id, String name);
}
