package com.example.sportspace.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.net.URI;
import java.sql.Connection;

@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    private final Environment env;

    public DataSourceConfig(Environment env) {
        this.env = env;
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        DataSource mySqlCandidate = null;
        try {
            mySqlCandidate = createMySqlCandidate();
            if (mySqlCandidate != null) {
                try (Connection conn = mySqlCandidate.getConnection()) {
                    if (conn.isValid(3)) {
                        log.info("MySQL connection established and verified successfully.");
                        return mySqlCandidate;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("MySQL connection initialization failed ({}: {}). Switching to in-memory H2 database so backend runs reliably.",
                    e.getClass().getSimpleName(), e.getMessage());
            if (mySqlCandidate instanceof HikariDataSource hikari) {
                try {
                    hikari.close();
                } catch (Exception ignored) {
                }
            }
        }

        // Safe Fallback to in-memory H2 database (guarantees NO CRASH on Railway or local machine)
        log.info("Initializing in-memory H2 database with MySQL compatibility mode.");
        HikariConfig h2Config = new HikariConfig();
        h2Config.setJdbcUrl("jdbc:h2:mem:sportspace;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_LOWER=TRUE");
        h2Config.setDriverClassName("org.h2.Driver");
        h2Config.setUsername("sa");
        h2Config.setPassword("");
        h2Config.setMaximumPoolSize(5);
        return new HikariDataSource(h2Config);
    }

    private DataSource createMySqlCandidate() {
        // 1. Check Railway / Cloud URL format (MYSQL_URL or DATABASE_URL)
        String rawDbUrl = getProperty("MYSQL_URL", "DATABASE_URL");
        if (rawDbUrl != null && !rawDbUrl.isBlank()) {
            try {
                if (rawDbUrl.startsWith("mysql://")) {
                    URI uri = URI.create(rawDbUrl);
                    String host = uri.getHost();
                    int port = uri.getPort() == -1 ? 3306 : uri.getPort();
                    String path = uri.getPath();
                    String db = (path != null && path.length() > 1) ? path.substring(1) : "railway";
                    String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + db + "?createDatabaseIfNotExist=true&serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false";

                    String username = "root";
                    String password = "";
                    if (uri.getUserInfo() != null) {
                        String[] parts = uri.getUserInfo().split(":", 2);
                        username = parts[0];
                        if (parts.length > 1) password = parts[1];
                    }
                    log.info("Configuring MySQL from Cloud URI (host: {}, port: {})", host, port);
                    return buildHikari(jdbcUrl, username, password);
                } else if (rawDbUrl.startsWith("jdbc:")) {
                    return buildHikari(rawDbUrl, getUsername(), getPassword());
                }
            } catch (Exception e) {
                log.warn("Failed to parse Cloud database URL: {}", e.getMessage());
            }
        }

        // 2. Check Railway MYSQLHOST variable
        String mysqlHost = env.getProperty("MYSQLHOST");
        if (mysqlHost != null && !mysqlHost.isBlank()) {
            String port = env.getProperty("MYSQLPORT", "3306");
            String db = env.getProperty("MYSQLDATABASE", "railway");
            String user = getUsername();
            String pass = getPassword();
            String jdbcUrl = "jdbc:mysql://" + mysqlHost + ":" + port + "/" + db + "?createDatabaseIfNotExist=true&serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false";
            log.info("Configuring MySQL from Railway MYSQLHOST: {}:{}", mysqlHost, port);
            return buildHikari(jdbcUrl, user, pass);
        }

        // 3. Check Railway Environment vs Local Environment
        boolean isRailway = env.getProperty("RAILWAY_ENVIRONMENT") != null || env.getProperty("RAILWAY_PROJECT_ID") != null;
        if (isRailway) {
            // In Railway private network, check internal mysql host
            String user = getUsername();
            String pass = getPassword();
            String db = env.getProperty("MYSQLDATABASE", "railway");
            String jdbcUrl = "jdbc:mysql://mysql.railway.internal:3306/" + db + "?createDatabaseIfNotExist=true&serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false";
            log.info("Railway environment detected. Attempting connection to internal MySQL host: mysql.railway.internal:3306");
            return buildHikari(jdbcUrl, user, pass);
        }

        // 4. Local machine MySQL
        String url = getProperty("spring.datasource.url", "SPRING_DATASOURCE_URL");
        if (url == null || url.isBlank()) {
            url = "jdbc:mysql://localhost:3306/sportspace_db?createDatabaseIfNotExist=true&serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false";
        }
        return buildHikari(url, getUsername(), getPassword());
    }

    private HikariDataSource buildHikari(String jdbcUrl, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setUsername(username);
        config.setPassword(password);
        config.setConnectionTimeout(3000); // 3 seconds timeout to fallback quickly if unreachable
        config.setInitializationFailTimeout(3000);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setMaxLifetime(1800000);
        config.setIdleTimeout(600000);
        return new HikariDataSource(config);
    }

    private String getUsername() {
        String val = getProperty("spring.datasource.username", "SPRING_DATASOURCE_USERNAME", "MYSQLUSER");
        return (val != null && !val.isBlank()) ? val : "root";
    }

    private String getPassword() {
        String val = getProperty("spring.datasource.password", "SPRING_DATASOURCE_PASSWORD", "MYSQLPASSWORD", "DB_PASSWORD");
        return (val != null) ? val : "root";
    }

    private String getProperty(String... keys) {
        for (String key : keys) {
            String val = env.getProperty(key);
            if (val != null && !val.isBlank()) {
                return val;
            }
        }
        return null;
    }
}
