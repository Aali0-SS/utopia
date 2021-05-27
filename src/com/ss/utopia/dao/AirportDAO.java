/**
 * 
 */
package com.ss.utopia.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ss.utopia.entity.Airport;

/**
 * Airport DAO to handle airport CRUD and connections.
 * 
 * @author ahmed
 */
public class AirportDAO extends MainDAO<Airport> {

	public AirportDAO(Connection conn) {
		super(conn);
	}

	public void addAirport(Airport airport) throws SQLException, ClassNotFoundException {
		save("INSERT INTO airport (iata_id, city) VALUES (?, ?)",
				new Object[] { airport.getAirportCode(), airport.getCityName() });
	}

	public void updateAirport(Airport airport) throws SQLException, ClassNotFoundException {
		save("UPDATE airport SET city = ? WHERE id = ? ",
				new Object[] { airport.getCityName(), airport.getAirportCode() });
	}

	public void deleteAirport(Airport airport) throws SQLException, ClassNotFoundException {
		save("DELETE FROM airport WHERE iata_id = ? ", new Object[] { airport.getAirportCode() });
	}

	public List<Airport> getAirports() throws SQLException, ClassNotFoundException {
		return read("SELECT * FROM airport", null);
	}

	
	public List<Airport> extractData(ResultSet rs) throws SQLException, ClassNotFoundException {
		List<Airport> airports = new ArrayList<>();
		while (rs.next()) {
			Airport airport = new Airport();
			airport.setAirportCode(rs.getString("iata_id"));
			airport.setCityName(rs.getString("city"));
			airports.add(airport);
		}
		return airports;
	}
}