CREATE TABLE IF NOT EXISTS gym_exercise (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(80) NOT NULL,
  target_muscle VARCHAR(80) DEFAULT NULL,
  equipment VARCHAR(80) DEFAULT NULL,
  difficulty VARCHAR(20) DEFAULT NULL,
  image_url VARCHAR(512) DEFAULT NULL,
  video_url VARCHAR(512) DEFAULT NULL,
  steps TEXT DEFAULT NULL,
  common_mistakes TEXT DEFAULT NULL,
  tips TEXT DEFAULT NULL,
  status CHAR(1) DEFAULT '0',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS gym_exercise_alternative (
  id BIGINT NOT NULL AUTO_INCREMENT,
  exercise_id BIGINT NOT NULL,
  alternative_exercise_id BIGINT NOT NULL,
  reason VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_exercise_id (exercise_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS gym_training_plan (
  id BIGINT NOT NULL AUTO_INCREMENT,
  member_id BIGINT NOT NULL,
  goal VARCHAR(80) DEFAULT NULL,
  weekly_frequency INT DEFAULT 3,
  source VARCHAR(20) DEFAULT 'SYSTEM',
  status CHAR(1) DEFAULT '0',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_member_id (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS gym_training_plan_day (
  id BIGINT NOT NULL AUTO_INCREMENT,
  plan_id BIGINT NOT NULL,
  day_index INT NOT NULL,
  title VARCHAR(100) DEFAULT NULL,
  target_muscle VARCHAR(120) DEFAULT NULL,
  estimated_minutes INT DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_plan_id (plan_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS gym_training_plan_item (
  id BIGINT NOT NULL AUTO_INCREMENT,
  plan_day_id BIGINT NOT NULL,
  exercise_id BIGINT DEFAULT NULL,
  sets INT DEFAULT NULL,
  reps VARCHAR(40) DEFAULT NULL,
  rest_seconds INT DEFAULT NULL,
  sort_order INT DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_plan_day_id (plan_day_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS gym_training_log (
  id BIGINT NOT NULL AUTO_INCREMENT,
  member_id BIGINT NOT NULL,
  plan_id BIGINT DEFAULT NULL,
  training_date DATETIME NOT NULL,
  duration_minutes INT DEFAULT NULL,
  intensity INT DEFAULT NULL,
  calories_burned INT DEFAULT NULL,
  feeling VARCHAR(80) DEFAULT NULL,
  remark VARCHAR(500) DEFAULT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_member_date (member_id, training_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS gym_training_log_item (
  id BIGINT NOT NULL AUTO_INCREMENT,
  log_id BIGINT NOT NULL,
  exercise_id BIGINT DEFAULT NULL,
  weight DECIMAL(10,2) DEFAULT NULL,
  reps INT DEFAULT NULL,
  sets INT DEFAULT NULL,
  completed CHAR(1) DEFAULT '1',
  PRIMARY KEY (id),
  KEY idx_log_id (log_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS gym_training_checkin (
  id BIGINT NOT NULL AUTO_INCREMENT,
  member_id BIGINT NOT NULL,
  plan_id BIGINT DEFAULT NULL,
  start_time DATETIME NOT NULL,
  end_time DATETIME DEFAULT NULL,
  duration_minutes INT DEFAULT NULL,
  status CHAR(1) DEFAULT '0',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_member_id (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS gym_course_feedback (
  id BIGINT NOT NULL AUTO_INCREMENT,
  member_id BIGINT NOT NULL,
  schedule_id BIGINT DEFAULT NULL,
  course_id BIGINT DEFAULT NULL,
  coach_id BIGINT DEFAULT NULL,
  rating INT DEFAULT NULL,
  intensity INT DEFAULT NULL,
  content VARCHAR(1000) DEFAULT NULL,
  tags VARCHAR(255) DEFAULT NULL,
  handle_status VARCHAR(32) DEFAULT 'pending',
  admin_reply VARCHAR(1000) DEFAULT NULL,
  follow_up_required TINYINT DEFAULT 0,
  handle_time DATETIME DEFAULT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_member_id (member_id),
  KEY idx_coach_id (coach_id),
  KEY idx_course_feedback_handle_status (handle_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS gym_diet_target (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  calories_target DECIMAL(10,2) DEFAULT NULL,
  protein_target DECIMAL(10,2) DEFAULT NULL,
  fat_target DECIMAL(10,2) DEFAULT NULL,
  carbohydrate_target DECIMAL(10,2) DEFAULT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS gym_gym_area (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(80) NOT NULL,
  capacity INT DEFAULT 0,
  location VARCHAR(120) DEFAULT NULL,
  status CHAR(1) DEFAULT '0',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS gym_traffic_snapshot (
  id BIGINT NOT NULL AUTO_INCREMENT,
  area_id BIGINT DEFAULT NULL,
  current_count INT DEFAULT 0,
  capacity INT DEFAULT 0,
  heat_level INT DEFAULT 0,
  snapshot_time DATETIME NOT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_area_time (area_id, snapshot_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
