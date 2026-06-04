SET @has_followup_chat_index := (
  SELECT COUNT(*)
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'gym_course_feedback'
    AND INDEX_NAME = 'idx_course_feedback_followup_chat'
);
SET @sql := IF(@has_followup_chat_index = 0,
  'CREATE INDEX idx_course_feedback_followup_chat ON gym_course_feedback (member_id, coach_id, follow_up_required)',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
