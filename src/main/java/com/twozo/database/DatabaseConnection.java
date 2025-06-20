package com.twozo.database;

import java.sql.Connection;
import java.sql.SQLException;

import com.twozo.exception.database.DatabaseException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConnection {
	
	private static final HikariDataSource DATA_SOURCE;

	static {
		try {
			HikariConfig config = new HikariConfig();
			config.setJdbcUrl("jdbc:postgresql://localhost:5432/banking_db");
			config.setUsername("postgres");
			config.setPassword("vimal");
			config.setDriverClassName("org.postgresql.Driver");
			config.setMaximumPoolSize(10);
			config.setMinimumIdle(2);
			config.setIdleTimeout(30000);
			config.setConnectionTimeout(20000);
			config.setMaxLifetime(1800000);

			DATA_SOURCE = new HikariDataSource(config);
			System.out.println("✅ DatabaseConnection initialized successfully!");
		} catch (Exception e) {
			System.err.println("❌ Database connection failed: " + e.getMessage());
			throw new ExceptionInInitializerError("Database connection failed: " + e.getMessage());
		}
	}

	public static Connection getConnection() throws DatabaseException {
		try {
			return DATA_SOURCE.getConnection();
		} catch (SQLException e) {
			throw new DatabaseException("Database connection error : ", e);
		}
	}
}