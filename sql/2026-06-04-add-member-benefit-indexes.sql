SET @has_member_card_benefit_index := (
  SELECT COUNT(*)
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'gym_member_card'
    AND INDEX_NAME = 'idx_member_card_benefit_lookup'
);
SET @sql := IF(@has_member_card_benefit_index = 0,
  'CREATE INDEX idx_member_card_benefit_lookup ON gym_member_card (member_id, status, deleted, expire_date)',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_equipment_status_location_index := (
  SELECT COUNT(*)
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'gym_equipment'
    AND INDEX_NAME = 'idx_equipment_status_location'
);
SET @sql := IF(@has_equipment_status_location_index = 0,
  'CREATE INDEX idx_equipment_status_location ON gym_equipment (status, location)',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_traffic_snapshot_area_time_index := (
  SELECT COUNT(*)
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'gym_traffic_snapshot'
    AND INDEX_NAME = 'idx_traffic_snapshot_area_time'
);
SET @sql := IF(@has_traffic_snapshot_area_time_index = 0,
  'CREATE INDEX idx_traffic_snapshot_area_time ON gym_traffic_snapshot (area_id, snapshot_time)',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
