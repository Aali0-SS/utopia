/**
 * 
 */
package com.ss.utopia.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author ahmed
 *
 */
public class Connector {

	public Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/utopia", "userDemo", "pass");
		conn.setAutoCommit(Boolean.FALSE);
		return conn;
	}

	public Connection getConnection(String connector) throws ClassNotFoundException, SQLException {
		Class.forName(connector + "com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection(connector + "jdbc:mysql://localhost:3306/utopia","userDemo", "pass");
		conn.setAutoCommit(Boolean.FALSE);
		return conn;
	}

}