package com.ss.utopia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Main DAO that extends other DAO methods to keep code DRY handles read and
 * save.
 * 
 * @author ahmed
 */

public abstract class MainDAO<T> {

	public static Connection conn = null;

	public MainDAO(Connection conn) {
		MainDAO.conn = conn;
	}

	public void save(String sql, Object[] val) throws ClassNotFoundException, SQLException {
		PreparedStatement pstmt = conn.prepareStatement(sql);
		if (val != null) {
			int count = 1;
			for (Object obj : val) {
				pstmt.setObject(count, obj);
				count++;
			}
		}
		pstmt.executeUpdate();
	}

	public List<T> read(String sql, Object[] val) throws ClassNotFoundException, SQLException {
		PreparedStatement pstmt = conn.prepareStatement(sql);
		if (val != null) {
			int count = 1;
			for (Object obj : val) {
				pstmt.setObject(count, obj);
				count++;
			}
		}
		return extractData(pstmt.executeQuery());
	}

	abstract public List<T> extractData(ResultSet rs) throws ClassNotFoundException, SQLException;
}