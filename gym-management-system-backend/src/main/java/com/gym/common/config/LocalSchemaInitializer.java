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
        ensureFeedbackColumns();
        ensureHealthIndexes();
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

    private void ensureFeedbackColumns() {
        List<ColumnDefinition> columns = List.of(
                new ColumnDefinition("booking_id", "bigint NULL"),
                new ColumnDefinition("feedback_type", "varchar(32) DEFAULT 'course'"),
                new ColumnDefinition("handle_status", "varchar(32) DEFAULT 'pending'"),
                new ColumnDefinition("admin_reply", "varchar(1000) NULL"),
                new ColumnDefinition("follow_up_required", "tinyint DEFAULT 0"),
                new ColumnDefinition("handle_time", "datetime NULL")
        );

        for (ColumnDefinition column : columns) {
            if (!columnExists("gym_course_feedback", column.name())) {
                jdbcTemplate.execute("ALTER TABLE gym_course_feedback ADD COLUMN " + column.name() + " " + column.definition());
            }
        }

        ensureIndex("gym_course_feedback", "idx_course_feedback_booking_id", "booking_id");
        ensureIndex("gym_course_feedback", "idx_course_feedback_type", "feedback_type");
        ensureIndex("gym_course_feedback", "idx_course_feedback_handle_status", "handle_status");
        ensureIndex("gym_course_feedback", "idx_course_feedback_followup_chat", "member_id, coach_id, follow_up_required");
    }

    private void ensureHealthIndexes() {
        ensureIndex("gym_health_data", "idx_health_data_user_measure", "user_id, measure_time");
        ensureIndex("gym_diet_log", "idx_diet_log_user_date", "user_id, eat_date");
        ensureIndex("gym_diet_target", "idx_diet_target_user", "user_id");
    }

    private void ensureIndex(String tableName, String indexName, String columnName) {
        if (!indexExists(tableName, indexName)) {
            jdbcTemplate.execute("CREATE INDEX " + indexName + " ON " + tableName + " (" + columnName + ")");
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

    private boolean indexExists(String tableName, String indexName) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.STATISTICS
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = ?
                  AND INDEX_NAME = ?
                """,
                Integer.class,
                tableName,
                indexName
        );
        return count != null && count > 0;
    }

    private record ColumnDefinition(String name, String definition) {
    }
}
