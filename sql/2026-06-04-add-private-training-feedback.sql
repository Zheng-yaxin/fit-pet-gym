SET @has_booking_id := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'gym_course_feedback'
    AND COLUMN_NAME = 'booking_id'
);
SET @sql := IF(@has_booking_id = 0,
  'ALTER TABLE gym_course_feedback ADD COLUMN booking_id BIGINT NULL AFTER schedule_id',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_feedback_type := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'gym_course_feedback'
    AND COLUMN_NAME = 'feedback_type'
);
SET @sql := IF(@has_feedback_type = 0,
  'ALTER TABLE gym_course_feedback ADD COLUMN feedback_type VARCHAR(32) DEFAULT ''course'' AFTER coach_id',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_booking_index := (
  SELECT COUNT(*)
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'gym_course_feedback'
    AND INDEX_NAME = 'idx_course_feedback_booking_id'
);
SET @sql := IF(@has_booking_index = 0,
  'CREATE INDEX idx_course_feedback_booking_id ON gym_course_feedback (booking_id)',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_type_index := (
  SELECT COUNT(*)
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'gym_course_feedback'
    AND INDEX_NAME = 'idx_course_feedback_type'
);
SET @sql := IF(@has_type_index = 0,
  'CREATE INDEX idx_course_feedback_type ON gym_course_feedback (feedback_type)',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
