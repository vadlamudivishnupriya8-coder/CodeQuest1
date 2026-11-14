package firstphase;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Base64;

/**
 * MySQL bootstrap and connection utility.
 * Change DB_USER / DB_PASSWORD below to match your local MySQL installation.
 */
public final class DBConnection {
    private static final String DB_NAME = "codequest_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root"; // Change this if your MySQL root password differs.
    private static final String SERVER_URL = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/" + DB_NAME + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final DBConnection INSTANCE = new DBConnection();

    private DBConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            initializeDatabase();
        } catch (Exception e) {
            throw new ExceptionInInitializerError("CodeQuest database initialization failed: " + e.getMessage());
        }
    }

    public static DBConnection getInstance() { return INSTANCE; }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private void initializeDatabase() throws Exception {
        try (Connection c = DriverManager.getConnection(SERVER_URL, DB_USER, DB_PASSWORD);
             Statement s = c.createStatement()) {
            s.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        }
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS admins (
                  id INT PRIMARY KEY AUTO_INCREMENT,
                  username VARCHAR(50) NOT NULL UNIQUE,
                  password VARCHAR(255) NOT NULL
                ) ENGINE=InnoDB
                """);
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                  id INT PRIMARY KEY AUTO_INCREMENT,
                  username VARCHAR(50) NOT NULL UNIQUE,
                  email VARCHAR(100) NOT NULL UNIQUE,
                  password VARCHAR(255) NOT NULL,
                  total_xp INT NOT NULL DEFAULT 0,
                  player_level INT NOT NULL DEFAULT 1,
                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                ) ENGINE=InnoDB
                """);
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS missions (
                  id INT PRIMARY KEY AUTO_INCREMENT,
                  title VARCHAR(100) NOT NULL,
                  description TEXT NOT NULL,
                  category VARCHAR(30) NOT NULL,
                  difficulty VARCHAR(20) NOT NULL,
                  option_a VARCHAR(255) NOT NULL,
                  option_b VARCHAR(255) NOT NULL,
                  option_c VARCHAR(255) NOT NULL,
                  option_d VARCHAR(255) NOT NULL,
                  correct_answer VARCHAR(255) NOT NULL,
                  xp_reward INT NOT NULL DEFAULT 50,
                  created_by INT NULL,
                  CONSTRAINT fk_mission_admin FOREIGN KEY (created_by) REFERENCES admins(id) ON DELETE SET NULL
                ) ENGINE=InnoDB
                """);
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS mission_submissions (
                  id INT PRIMARY KEY AUTO_INCREMENT,
                  user_id INT NOT NULL,
                  mission_id INT NOT NULL,
                  answer VARCHAR(255) NOT NULL,
                  is_correct BOOLEAN NOT NULL DEFAULT FALSE,
                  completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                  CONSTRAINT fk_submission_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                  CONSTRAINT fk_submission_mission FOREIGN KEY (mission_id) REFERENCES missions(id) ON DELETE CASCADE,
                  UNIQUE KEY unique_user_mission (user_id, mission_id)
                ) ENGINE=InnoDB
                """);
        }
        seedAdmin();
        seedMissions();
    }

    private void seedAdmin() throws Exception {
        try (Connection c = getConnection();
             PreparedStatement check = c.prepareStatement("SELECT id FROM admins WHERE username=?")) {
            check.setString(1, "admin");
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) return;
            }
            try (PreparedStatement insert = c.prepareStatement("INSERT INTO admins(username,password) VALUES(?,?)")) {
                insert.setString(1, "admin");
                insert.setString(2, hashPassword("admin123"));
                insert.executeUpdate();
            }
        }
    }

    private void seedMissions() throws SQLException {
        String countSql = "SELECT COUNT(*) FROM missions";
        try (Connection c = getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(countSql)) {
            if (rs.next() && rs.getInt(1) > 0) return;
        }
        String sql = "INSERT INTO missions(title,description,category,difficulty,option_a,option_b,option_c,option_d,correct_answer,xp_reward,created_by) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        Object[][] q = {
            {"Java: Main Method","Which method is the entry point of a standard Java application?","Java","Easy","start()","main()","run()","init()","main()",50},
            {"Java: String Length","Which method returns the number of characters in a Java String?","Java","Easy","size()","count()","length()","capacity()","length()",50},
            {"SQL: Read Records","Which SQL command retrieves rows from a table?","SQL","Easy","GET","SELECT","OPEN","FETCH ALL","SELECT",50},
            {"DSA: FIFO","Which data structure follows First In, First Out?","DSA","Easy","Stack","Queue","Tree","Graph","Queue",60},
            {"DSA: Binary Search","What is the time complexity of binary search on a sorted array?","DSA","Medium","O(n)","O(n log n)","O(log n)","O(1)","O(log n)",75}
        };
        try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            for (Object[] row : q) {
                for (int i=0;i<row.length;i++) {
                    if (row[i] instanceof Integer) p.setInt(i+1,(Integer)row[i]);
                    else p.setString(i+1,String.valueOf(row[i]));
                }
                p.setInt(11, 1);
                p.addBatch();
            }
            p.executeBatch();
        }
    }

    public static String hashPassword(String password) throws Exception {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        var spec = new javax.crypto.spec.PBEKeySpec(password.toCharArray(), salt, 120000, 256);
        byte[] hash = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyPassword(String password, String stored) throws Exception {
        if (stored == null || !stored.contains(":")) return false;
        String[] parts = stored.split(":", 2);
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] expected = Base64.getDecoder().decode(parts[1]);
        var spec = new javax.crypto.spec.PBEKeySpec(password.toCharArray(), salt, 120000, expected.length * 8);
        byte[] actual = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        return MessageDigest.isEqual(expected, actual);
    }
}
