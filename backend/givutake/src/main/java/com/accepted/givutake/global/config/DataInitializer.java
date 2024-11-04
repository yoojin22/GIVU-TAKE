package com.accepted.givutake.global.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Component
public class DataInitializer {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    private final ExperienceVillage experienceVillage;
    private final Region region;

    @Autowired
    public DataInitializer(JdbcTemplate jdbcTemplate, DataSource dataSource, ExperienceVillage experienceVillage, Region region) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
        this.experienceVillage = experienceVillage;
        this.region = region;

    }

    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    public void initialize() {
        if (!isDataInitialized()) {
            executeDataSql();
            markDataAsInitialized();
            region.processRegionData();
            experienceVillage.processExperienceVillageData();
        }
    }


    private boolean isDataInitialized() {
        try {
            jdbcTemplate.queryForObject("SELECT COUNT(*) FROM initialization_flag", Integer.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void executeDataSql() {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator(false, false, "UTF-8", new ClassPathResource("data.sql"));
        resourceDatabasePopulator.execute(dataSource);
    }

    private void markDataAsInitialized() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS initialization_flag (initialized BOOLEAN)");
        jdbcTemplate.update("INSERT INTO initialization_flag VALUES (TRUE)");
    }
}