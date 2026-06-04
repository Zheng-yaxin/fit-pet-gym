package com.gym.tools;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseImporter {
    private static final String ROOT_URL = "jdbc:mysql://localhost:3306/?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true";
    private static final String GYM_URL = "jdbc:mysql://localhost:3306/gym?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true";

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            throw new IllegalArgumentException("Pass --probe or one or more SQL file paths.");
        }

        if ("--probe".equals(args[0])) {
            probe();
            return;
        }

        try (Connection root = DriverManager.getConnection(ROOT_URL, "root", "1234");
             Statement statement = root.createStatement()) {
            statement.execute("CREATE DATABASE IF NOT EXISTS gym DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci");
        }

        try (Connection gym = DriverManager.getConnection(GYM_URL, "root", "1234")) {
            for (String arg : args) {
                Path path = Path.of(arg);
                String sql = Files.readString(path, StandardCharsets.UTF_8);
                int executed = executeSql(gym, sql);
                System.out.println("Imported " + path + " (" + executed + " statements)");
            }
        }
    }

    private static void probe() throws SQLException {
        try (Connection root = DriverManager.getConnection(ROOT_URL, "root", "1234");
             Statement statement = root.createStatement()) {
            try (var rs = statement.executeQuery("SHOW DATABASES LIKE 'gym'")) {
                if (!rs.next()) {
                    System.out.println("gym database missing");
                    return;
                }
            }
        }

        try (Connection gym = DriverManager.getConnection(GYM_URL, "root", "1234");
             Statement statement = gym.createStatement();
             var rs = statement.executeQuery("SHOW TABLES")) {
            int count = 0;
            List<String> names = new ArrayList<>();
            while (rs.next()) {
                count++;
                if (names.size() < 12) {
                    names.add(rs.getString(1));
                }
            }
            System.out.println("gym database exists, tableCount=" + count + ", sampleTables=" + names);
        }
    }

    private static int executeSql(Connection connection, String sql) throws SQLException {
        int executed = 0;
        for (String statementSql : splitStatements(sql)) {
            String trimmed = statementSql.trim();
            if (trimmed.isEmpty()) continue;
            try (Statement statement = connection.createStatement()) {
                statement.execute(trimmed);
                executed++;
            }
        }
        return executed;
    }

    private static List<String> splitStatements(String sql) {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean singleQuoted = false;
        boolean doubleQuoted = false;
        boolean lineComment = false;
        boolean blockComment = false;

        for (int index = 0; index < sql.length(); index++) {
            char c = sql.charAt(index);
            char next = index + 1 < sql.length() ? sql.charAt(index + 1) : '\0';

            if (lineComment) {
                if (c == '\n' || c == '\r') {
                    lineComment = false;
                    current.append(c);
                }
                continue;
            }

            if (blockComment) {
                if (c == '*' && next == '/') {
                    blockComment = false;
                    index++;
                }
                continue;
            }

            if (!singleQuoted && !doubleQuoted && c == '-' && next == '-') {
                lineComment = true;
                index++;
                continue;
            }

            if (!singleQuoted && !doubleQuoted && c == '/' && next == '*') {
                blockComment = true;
                index++;
                continue;
            }

            current.append(c);

            if (c == '\'' && !doubleQuoted && !isEscaped(sql, index)) {
                singleQuoted = !singleQuoted;
            } else if (c == '"' && !singleQuoted && !isEscaped(sql, index)) {
                doubleQuoted = !doubleQuoted;
            } else if (c == ';' && !singleQuoted && !doubleQuoted) {
                statements.add(current.substring(0, current.length() - 1));
                current.setLength(0);
            }
        }

        if (!current.toString().trim().isEmpty()) {
            statements.add(current.toString());
        }
        return statements;
    }

    private static boolean isEscaped(String value, int index) {
        int slashCount = 0;
        for (int i = index - 1; i >= 0 && value.charAt(i) == '\\'; i--) {
            slashCount++;
        }
        return slashCount % 2 == 1;
    }
}
