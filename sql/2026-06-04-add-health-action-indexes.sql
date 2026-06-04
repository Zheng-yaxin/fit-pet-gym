-- Health action-plan and body-insight read path indexes.
-- These endpoints reuse existing health, diet log, and diet target data.

CREATE INDEX idx_health_data_user_measure ON gym_health_data (user_id, measure_time);
CREATE INDEX idx_diet_log_user_date ON gym_diet_log (user_id, eat_date);
CREATE INDEX idx_diet_target_user ON gym_diet_target (user_id);
