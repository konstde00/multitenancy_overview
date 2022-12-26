package com.konstde00.lab.repository.dao;

import com.konstde00.lab.domain.dto.request.ResearchDto;
import com.konstde00.tenant_management.repository.dao.AbstractDao;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ResearchDao extends AbstractDao {

    protected ResearchDao(DataSource dataSource) {
        super(dataSource);
    }

    public List<ResearchDto> findAll() {

        String query = """
                
                select id, name, description
                from researches
                
                """;

        return namedParameterJdbcTemplate.query(query, (rs, rowNum) -> toDto(rs));
    }

    private ResearchDto toDto(ResultSet resultSet) throws SQLException {

        return ResearchDto
                .builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .build();
    }
}
