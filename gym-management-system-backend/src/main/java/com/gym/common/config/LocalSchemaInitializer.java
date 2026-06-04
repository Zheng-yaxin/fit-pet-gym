package com.gym.common.config;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocalSchemaInitializer {
    private final JdbcTemplate jdbcTemplate;

    public LocalSchemaInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void initialize() {
        ensureExerciseColumns();
        ensureMemberGrowthTable();
    }

    private void ensureMemberGrowthTable() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS gym_member_growth (
                  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                  member_id BIGINT NOT NULL,
                  total_xp INT DEFAULT 0,
                  level INT DEFAULT 1,
                  current_level_xp INT DEFAULT 0,
                  next_level_xp INT DEFAULT 500,
                  progress_percent INT DEFAULT 0,
                  streak_days INT DEFAULT 0,
                  total_sessions INT DEFAULT 0,
                  weekly_minutes INT DEFAULT 0,
                  weekly_sessions INT DEFAULT 0,
                  last_training_date DATETIME NULL,
                  pet_mood VARCHAR(32) DEFAULT 'idle',
                  badge_title VARCHAR(120) DEFAULT NULL,
                  last_reward_xp INT DEFAULT 0,
                  last_reward_reason VARCHAR(160) DEFAULT NULL,
                  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                  UNIQUE KEY uk_member_growth_member_id (member_id),
                  KEY idx_member_growth_level (level, total_xp)
                )
                """);
    }

    private void ensureExerciseColumns() {
        List<ColumnDefinition> columns = List.of(
                new ColumnDefinition("primary_muscle", "varchar(80) NULL"),
                new ColumnDefinition("secondary_muscles", "varchar(255) NULL"),
                new ColumnDefinition("movement_pattern", "varchar(80) NULL"),
                new ColumnDefinition("steps", "text NULL"),
                new ColumnDefinition("common_mistakes", "text NULL"),
                new ColumnDefinition("alternatives", "varchar(255) NULL"),
                new ColumnDefinition("media_license", "varchar(120) NULL"),
                new ColumnDefinition("tips", "text NULL")
        );

        for (ColumnDefinition column : columns) {
            if (!columnExists("gym_exercise", column.name())) {
                jdbcTemplate.execute("ALTER TABLE gym_exercise ADD COLUMN " + column.name() + " " + column.definition());
            }
        }
    }

    private boolean columnExists(String tableName, String columnName) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = ?
                  AND COLUMN_NAME = ?
                """,
                Integer.class,
                tableName,
                columnName
        );
        return count != null && count > 0;
    }

    private record ColumnDefinition(String name, String definition) {
    }
}
