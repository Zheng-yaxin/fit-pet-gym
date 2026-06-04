SET @has_handle_status := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'gym_course_feedback'
    AND COLUMN_NAME = 'handle_status'
);
SET @sql := IF(@has_handle_status = 0,
  'ALTER TABLE gym_course_feedback ADD COLUMN handle_status VARCHAR(32) DEFAULT ''pending'' AFTER tags',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_admin_reply := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'gym_course_feedback'
    AND COLUMN_NAME = 'admin_reply'
);
SET @sql := IF(@has_admin_reply = 0,
  'ALTER TABLE gym_course_feedback ADD COLUMN admin_reply VARCHAR(1000) NULL AFTER handle_status',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_follow_up_required := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'gym_course_feedback'
    AND COLUMN_NAME = 'follow_up_required'
);
SET @sql := IF(@has_follow_up_required = 0,
  'ALTER TABLE gym_course_feedback ADD COLUMN follow_up_required TINYINT DEFAULT 0 AFTER admin_reply',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_handle_time := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'gym_course_feedback'
    AND COLUMN_NAME = 'handle_time'
);
SET @sql := IF(@has_handle_time = 0,
  'ALTER TABLE gym_course_feedback ADD COLUMN handle_time DATETIME NULL AFTER follow_up_required',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_handle_index := (
  SELECT COUNT(*)
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'gym_course_feedback'
    AND INDEX_NAME = 'idx_course_feedback_handle_status'
);
SET @sql := IF(@has_handle_index = 0,
  'CREATE INDEX idx_course_feedback_handle_status ON gym_course_feedback (handle_status)',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
