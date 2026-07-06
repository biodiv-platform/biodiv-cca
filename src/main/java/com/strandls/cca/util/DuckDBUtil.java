package com.strandls.cca.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for managing DuckDB connections and resources.
 * Ensures proper resource cleanup to prevent native memory leaks.
 */
public class DuckDBUtil {

	private static final Logger logger = LoggerFactory.getLogger(DuckDBUtil.class);
	private static final String JDBC_URL = "jdbc:duckdb:";

	static {
		try {
			// Explicitly load the DuckDB JDBC driver once
			Class.forName("org.duckdb.DuckDBDriver");
			logger.info("DuckDB JDBC driver loaded successfully");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Failed to load DuckDB JDBC driver", e);
		}
	}

	/**
	 * Creates a new DuckDB connection with spatial extension loaded.
	 *
	 * @return Connection with spatial extension enabled
	 * @throws SQLException if connection or extension loading fails
	 */
	public static Connection createConnectionWithSpatial() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = DriverManager.getConnection(JDBC_URL);

			// Install and load spatial extension
			stmt = conn.createStatement();
			stmt.execute("INSTALL spatial;");
			stmt.execute("LOAD spatial;");

			logger.debug("DuckDB connection created with spatial extension loaded");
			return conn;

		} catch (SQLException e) {
			// Clean up resources on error
			closeQuietly(stmt);
			closeQuietly(conn);
			throw e;
		} finally {
			// Always close the statement used for setup
			closeQuietly(stmt);
		}
	}

	/**
	 * Closes a SQL statement quietly without throwing exceptions.
	 *
	 * @param stmt Statement to close, can be null
	 */
	public static void closeQuietly(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				logger.warn("Error closing statement", e);
			}
		}
	}

	/**
	 * Closes a connection quietly without throwing exceptions.
	 *
	 * @param conn Connection to close, can be null
	 */
	public static void closeQuietly(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				logger.warn("Error closing connection", e);
			}
		}
	}

	/**
	 * Interface for executing operations with a DuckDB connection.
	 * Ensures proper resource cleanup.
	 *
	 * @param <T> Return type of the operation
	 */
	@FunctionalInterface
	public interface DuckDBOperation<T> {
		T execute(Connection conn) throws Exception;
	}

	/**
	 * Executes an operation with a DuckDB connection that has spatial extension loaded.
	 * Automatically handles connection creation and cleanup.
	 *
	 * @param <T> Return type
	 * @param operation Operation to execute
	 * @return Result of the operation
	 * @throws Exception if operation fails
	 */
	public static <T> T withSpatialConnection(DuckDBOperation<T> operation) throws Exception {
		try (Connection conn = createConnectionWithSpatial()) {
			return operation.execute(conn);
		}
	}
}
