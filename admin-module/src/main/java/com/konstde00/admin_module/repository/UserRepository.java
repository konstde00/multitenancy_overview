package com.konstde00.admin_module.repository;

import com.konstde00.commons.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
