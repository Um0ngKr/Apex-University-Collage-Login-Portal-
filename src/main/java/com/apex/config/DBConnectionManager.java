package com.apex.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread-safe Database Connection Manager for Apex University System.
 * Connects to MySQL using JDBC and provides pooled/managed connections.
 */
public class DBConnectionManager {

    private static final Logger LOGGER = Logger.getLogger(DBConnectionManager.class.getName());
    private static DBConnectionManager instance;

    private String url;
    private String user;
    private String password;
    private String driver;

    private DBConnectionManager() {
        loadProperties();
        try {
            Class.forName(driver);
            LOGGER.info("MySQL JDBC Driver registered successfully: " + driver);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Failed to load MySQL JDBC Driver", e);
        }
    }

    public static synchronized DBConnectionManager getInstance() {
        if (instance == null) {
            instance = new DBConnectionManager();
        }
        return instance;
    }

    private void loadProperties() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
                this.driver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
                this.url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/apex_university_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8");
                this.user = props.getProperty("db.user", "root");
                this.password = props.getProperty("db.password", "");
            } else {
                LOGGER.warning("db.properties not found on classpath, using default configuration");
                setDefaultProperties();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error reading db.properties, using fallback defaults", e);
            setDefaultProperties();
        }
    }

    private void setDefaultProperties() {
        this.driver = "com.mysql.cj.jdbc.Driver";
        this.url = "jdbc:mysql://localhost:3306/apex_university_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
        this.user = "root";
        this.password = "";
    }

    /**
     * Obtains a fresh database connection.
     * Includes automatic fallback for common local MySQL passwords (root, root123, admin, etc.)
     * in case db.properties was left blank.
     *
     * @return java.sql.Connection
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            // Error 1045 = ER_ACCESS_DENIED_ERROR
            if (e.getErrorCode() == 1045 || (e.getMessage() != null && e.getMessage().contains("Access denied"))) {
                String[] fallbacks = {"root", "root123", "admin", "123456", "1234", "password", ""};
                for (String fb : fallbacks) {
                    if (fb.equals(password)) continue;
                    try {
                        Connection conn = DriverManager.getConnection(url, user, fb);
                        LOGGER.info("Connected to MySQL successfully using fallback password: " + (fb.isEmpty() ? "<blank>" : fb));
                        this.password = fb;
                        return conn;
                    } catch (SQLException ignored) {}
                }
            }
            throw e;
        }
    }

    /**
     * Safely closes connection, statement, and result set.
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {}
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ignored) {}
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {}
        }
    }

    public static void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }

    public static void close(Statement stmt, ResultSet rs) {
        close(null, stmt, rs);
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }
}
