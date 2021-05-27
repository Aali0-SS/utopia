/**
 * 
 */
package com.ss.utopia.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ss.utopia.entity.User;

/**
 * UserDAO class to handle CRUD and connections with persistence layer.
 * 
 * @author ahmed
 *
 */
public class UserDAO extends MainDAO<User> {

	public UserDAO(Connection conn) {
		super(conn);
	}

	public Boolean addUser(User user) throws SQLException, ClassNotFoundException {
		Boolean executed = Boolean.FALSE;
		save("INSERT INTO user (`role_id`, `given_name`, `family_name`, `username`, `email`, `password`, `phone`) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)",
				new Object[] { user.getRole(), user.getGivenName(), user.getFamilyName(), user.getUsername(),
						user.getEmail(), user.getPassword(), user.getPhoneNumber() });

		executed = Boolean.TRUE;
		return executed;
	}

	public List<User> getUsers() throws SQLException, ClassNotFoundException {
		List<User> users = new ArrayList<>();
		users = read("SELECT * FROM user", null);
		return users;
	}

	public User findUser(User user) throws SQLException, ClassNotFoundException {
		List<User> users = new ArrayList<>();
		users = read("SELECT * FROM user WHERE username = ? OR id = ?",
				new Object[] { user.getUsername(), user.getUserId() });
		return users.get(0);
	}

	public Boolean updateUser(User user) throws SQLException, ClassNotFoundException {
		Boolean executed = Boolean.FALSE;
		if (user.getPassword() != null) {
			save("UPDATE user SET role_id = ?, given_name = ?, family_name = ?, username = ?, "
					+ "email = ?, phone = ?, password = ? WHERE id = ? or username = ?",
					new Object[] { user.getRole(), user.getGivenName(), user.getFamilyName(), user.getUsername(),
							user.getEmail(), user.getPhoneNumber(), user.getPassword(), user.getUserId(),
							user.getUsername() });
		} else {
			save("UPDATE user SET role_id = ?, given_name = ?, family_name = ?, username = ?, "
					+ "email = ?, phone = ? WHERE id = ? or username = ?",
					new Object[] { user.getRole(), user.getGivenName(), user.getFamilyName(), user.getUsername(),
							user.getEmail(), user.getPhoneNumber(), user.getUserId(), user.getUsername() });
		}
		executed = Boolean.TRUE;
		return executed;
	}

	public Boolean removeUser(User user) throws SQLException, ClassNotFoundException {
		Boolean executed = Boolean.FALSE;
		save("DELETE FROM user WHERE username = ?", new Object[] { user.getUsername() });
		executed = Boolean.TRUE;
		return executed;
	}

	
	public List<User> extractData(ResultSet rs) throws SQLException {
		List<User> users = new ArrayList<>();

		while (rs.next()) {
			User user = new User();
			user.setUserId(rs.getInt("id"));
			user.setRole(rs.getInt("role_id"));
			user.setGivenName(rs.getString("given_name"));
			user.setFamilyName(rs.getString("family_name"));
			user.setUsername(rs.getString("username"));
			user.setEmail(rs.getString("email"));
			user.setPhoneNumber(rs.getString("phone"));
			users.add(user);
		}

		return users;
	}
}