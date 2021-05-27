/**
 * 
 */
package com.ss.utopia.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.ss.utopia.dao.FlightDAO;
import com.ss.utopia.entity.Flight;
import com.ss.utopia.main.Index;

/**
 * @author ahmed
 *
 */
public class AgentService {

	public List<Flight> displayflights(Index app) {
		List<Flight> flights = null;
		try {
			Connection conn = app.getConnUtil().getConnection();
			FlightDAO fdao = new FlightDAO(conn);
			flights = fdao.getFlights();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return flights;
	}

	public Boolean updateFlight(Index app, Flight flight) throws SQLException {
		Connection conn = null;
		Boolean success = Boolean.FALSE;
		try {
			conn = app.getConnUtil().getConnection();
			FlightDAO fdao = new FlightDAO(conn);
			fdao.updateFlight(flight);
			success = !success;
			conn.commit();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			conn.rollback();
		} finally {
			conn.close();
		}
		return success;
	}
}