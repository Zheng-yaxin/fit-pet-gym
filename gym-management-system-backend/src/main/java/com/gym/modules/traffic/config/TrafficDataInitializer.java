package com.gym.modules.traffic.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class TrafficDataInitializer {
    private static final Logger log = LoggerFactory.getLogger(TrafficDataInitializer.class);
    private final JdbcTemplate jdbc;

    public TrafficDataInitializer(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    public void seedIfEmpty() {
        try {
            ensureTables();
            if (countSnapshots() > 0) return;

            List<AreaRow> areas = jdbc.query(
                "SELECT id, name, capacity FROM gym_gym_area",
                (rs, rowNum) -> new AreaRow(rs.getLong("id"), rs.getString("name"), rs.getInt("capacity"))
            );
            if (areas.isEmpty()) return;

            Random random = new Random();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            int currentHour = now.getHour();

            for (int hour = 6; hour <= Math.min(currentHour + 2, 22); hour++) {
                LocalDateTime time = now.withHour(hour).withMinute(0).withSecond(0);
                for (AreaRow area : areas) {
                    int cap = area.capacity > 0 ? area.capacity : 30;
                    int count = (int)((0.25 + random.nextDouble() * 0.6) * cap);
                    int heat = count > cap * 0.7 ? 3 : count > cap * 0.4 ? 2 : 1;
                    jdbc.update(
                        "INSERT INTO gym_traffic_snapshot (area_id, current_count, capacity, heat_level, snapshot_time, create_time) VALUES (?,?,?,?,?,NOW())",
                        area.id, count, cap, heat, time.format(fmt)
                    );
                }
            }
            log.info("Seeded {} traffic snapshots for {} areas", countSnapshots(), areas.size());
        } catch (Exception e) {
            log.warn("Traffic seeding skipped: {}", e.getMessage());
        }
    }

    private void ensureTables() {
        jdbc.execute("CREATE TABLE IF NOT EXISTS gym_gym_area (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, capacity INT DEFAULT 30, " +
            "location VARCHAR(200), create_time DATETIME DEFAULT NOW())");
        jdbc.execute("CREATE TABLE IF NOT EXISTS gym_traffic_snapshot (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, area_id BIGINT, current_count INT DEFAULT 0, " +
            "capacity INT, heat_level INT, snapshot_time DATETIME, create_time DATETIME DEFAULT NOW())");
        // Seed areas if empty
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM gym_gym_area", Integer.class);
        if (count == null || count == 0) {
            jdbc.update("INSERT INTO gym_gym_area (name, capacity, location) VALUES (?,?,?)", "力量训练区", 35, "一层东侧");
            jdbc.update("INSERT INTO gym_gym_area (name, capacity, location) VALUES (?,?,?)", "有氧区", 30, "一层西侧");
            jdbc.update("INSERT INTO gym_gym_area (name, capacity, location) VALUES (?,?,?)", "团课操房", 25, "二层A厅");
            jdbc.update("INSERT INTO gym_gym_area (name, capacity, location) VALUES (?,?,?)", "自由重量区", 20, "一层中部");
            jdbc.update("INSERT INTO gym_gym_area (name, capacity, location) VALUES (?,?,?)", "拉伸区", 15, "一层南侧");
            log.info("Seeded 5 gym areas");
        }
    }

    private int countSnapshots() {
        Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM gym_traffic_snapshot", Integer.class);
        return c != null ? c : 0;
    }

    private record AreaRow(long id, String name, int capacity) {}
}