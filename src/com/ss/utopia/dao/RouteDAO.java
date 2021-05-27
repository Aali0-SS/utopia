package com.ss.utopia.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ss.utopia.entity.Airport;
import com.ss.utopia.entity.Route;

public class RouteDAO extends MainDAO<Route> {

	public RouteDAO(Connection conn) {
		super(conn);
	}

	public void addRoute(Route route) throws ClassNotFoundException, SQLException {
		save("INSERT INTO route (origin_id, destination_id) VALUES (?, ?)",
				new Object[] { route.getOrigin().getAirportCode(), route.getDestination().getAirportCode() });
	}

	public void updateRoute(Route route) throws ClassNotFoundException, SQLException {
		save("UPDATE route SET origin_id = ?, destination_id = ? WHERE id = ?", new Object[] {
				route.getOrigin().getAirportCode(), route.getDestination().getAirportCode(), route.getId() });
	}

	public void deleteRoute(Route route) throws ClassNotFoundException, SQLException {
		save("DELETE FROM route WHERE id = ?", new Object[] { route.getId() });
	}

	public List<Route> readRoutes() throws ClassNotFoundException, SQLException {
		return read("SELECT * FROM route", null);
	}

	public List<Route> readRoutesByAirportCode(String airportCode) throws ClassNotFoundException, SQLException {
		return read("SELECT * FROM route WHERE origin_id = ? OR destination_id = ?",
				new Object[] { airportCode, airportCode });
	}


	public List<Route> extractData(ResultSet rs) throws ClassNotFoundException, SQLException {
		List<Route> routes = new ArrayList<>();
		AirportDAO adao = new AirportDAO(conn);
		List<Airport> airports = adao.getAirports();

		while (rs.next()) {
			Route route = new Route();
			route.setId(rs.getInt("id"));
			StringBuilder originId = new StringBuilder();
			originId.append(rs.getString("origin_id"));
			StringBuilder destinationId = new StringBuilder();
			destinationId.append(rs.getString("destination_id"));

			for (Airport airport : airports) {
				if (airport.getAirportCode().equals(originId.toString())) {
					route.setOrigin(airport);
				} else if (airport.getAirportCode().equals(destinationId.toString())) {
					route.setDestination(airport);
				}
			}
			routes.add(route);
		}
		return routes;
	}
}