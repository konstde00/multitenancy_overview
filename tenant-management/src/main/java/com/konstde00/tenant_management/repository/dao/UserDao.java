package com.konstde00.tenant_management.repository.dao;

import com.konstde00.tenant_management.domain.dto.response.UserAuthShortDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserDao extends AbstractDao {

    @Autowired
    public UserDao(DataSource dataSource) {
        super(dataSource);
    }

    public UserAuthShortDto getAuthShortDtoByUserId(Long userId) {

        UserAuthShortDto dto = new UserAuthShortDto();

        String queryForTenantKey = """

            select u.tenant_id
            from users u
            where u.id = :userId

            """;

        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);

        Long tenantId = jdbcTemplate.queryForObject(queryForTenantKey, params, (rs, rowNum)
                -> rs.getLong("tenant_id"));

        dto.setTenantId(tenantId);
        dto.setRoles(getAuthoritiesByUserId(userId));

        return dto;
    }

    public List<String> getAuthoritiesByUserId(Long userId) {

        String queryForAuthorities = """

            select ur.role
            from user_roles ur
            where ur.user_id = :userId

            """;

        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);

        return jdbcTemplate.query(queryForAuthorities, params,
                (rs, rowNum) -> rs.getString("role"));
    }
}
