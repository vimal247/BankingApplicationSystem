package com.twozo.util;

import java.sql.Connection;
import java.sql.SQLException;

import com.twozo.exception.database.DatabaseException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConnection {
	
	private static HikariDataSource dataSource;

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

			dataSource = new HikariDataSource(config);
			System.out.println("✅ DatabaseConnection initialized successfully!");
		} catch (Exception e) {
			System.err.println("❌ Database connection failed: " + e.getMessage());
			throw new ExceptionInInitializerError("Database connection failed: " + e.getMessage());
		}
	}

	public static final Connection getConnection() throws DatabaseException {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			System.out.println("catch block executing..");
			throw new DatabaseException("Database connection error : ", e);
		}
	}
}