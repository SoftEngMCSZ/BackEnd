package me.whatdo.app.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseUtil {
	public final static String jdbcTag = "jdbc:postgresql://";
	public final static String dbPort = "5432";
//	public final static String multiQueries = "?allowMultiQueries=true";

	public final static String lambdaTesting = "lambdaTesting";
	public final static String dbName = "whatdo";
	public final static String testName = "test";

	// pooled across all usages.
	static Connection conn;

	/**
	 * Singleton access to DB connection to share resources effectively across multiple accesses.
	 */
	protected static Connection connect() throws Exception {
		if (conn != null) { return conn; }

		// this is resistant to any SQL-injection attack.
		String schemaName = dbName;
		String test = System.getenv("lambdaTesting");
		if (test != null) {
			schemaName = testName;
		}

		// These three environment variables must be set!
		String dbUser = System.getenv("WHATDO_DB_USER");
		if (dbUser == null) {
			System.err.println("Environment variable WHATDO_DB_USER is not set!");
		}
		String dbPwd = System.getenv("WHATDO_DB_PWD");
		if (dbPwd == null) {
			System.err.println("Environment variable WHATDO_DB_PWD is not set!");
		}
		String dbEndpoint = System.getenv("WHATDO_DB_ENDPOINT");
		if (dbEndpoint == null) {
			System.err.println("Environment variable WHATDO_DB_ENDPOINT is not set!");
		}

		try {
			conn = DriverManager.getConnection(
					jdbcTag + dbEndpoint + ":" + dbPort + "/" + schemaName /* + multiQueries */,
					dbUser,
					dbPwd);
			return conn;
		} catch (Exception ex) {
			System.err.println("DB-ERROR:" + schemaName + "," + dbUser + "," + dbPwd + "," + dbEndpoint);
			throw new Exception("Failed in database connection");
		}
	}
}
