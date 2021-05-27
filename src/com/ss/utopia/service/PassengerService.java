/**
 * 
 */
package com.ss.utopia.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.ss.utopia.dao.BookingDAO;
import com.ss.utopia.dao.FlightDAO;
import com.ss.utopia.entity.Booking;
import com.ss.utopia.entity.Flight;
import com.ss.utopia.main.Index;

/**
 * @author ahmed
 *
 */
public class PassengerService {

	public Boolean addBooking(Index app) throws SQLException {
		Boolean executed = Boolean.FALSE;
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		Connection conn = null;
		List<Flight> tempFlights = new ArrayList<>();
		Integer option = 0;
		Booking booking = new Booking();
		booking.setIsActive(1);
		booking.setBookedBy(app.getUser());
		booking.setRefunded(0);
		booking.setStripeId("randomString");
		booking.setFlights(tempFlights);

		try {
			conn = app.getConnUtil().getConnection();
			FlightDAO fdao = new FlightDAO(conn);
			List<Flight> flights = fdao.getFlights();
			int counter = 1;
			int exit = -1;
			System.out.println("Choose a flight:");
			for (Flight flight : flights) {
				System.out.println(counter + ") From " + flight.getRoute().getOrigin().getCityName() + " to "
						+ flight.getRoute().getDestination().getCityName() + " on " + flight.getDepartureDateTime());
				counter++;
			}
			exit = counter;
			System.out.println(exit + ") Quit to Previous");
			System.out.print("Selection: ");
			option = scanner.nextInt();
			if (!option.equals(exit)) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
				LocalDateTime ldt = LocalDateTime.now();
				Flight flight = flights.get(option - 1);
				booking.setConfirmationCode(formatter.format(ldt));
				System.out.println("Seat selection:");
				System.out.println("1) View Flight Details");
				System.out.println("2) First Class");
				System.out.println("3) Business Class");
				System.out.println("4) Economy Class");
				System.out.println("5) Quit to Previous");
				System.out.print("Selection: ");
				option = scanner.nextInt();
				if (option.equals(1)) {
					System.out.println(flight.toString());
				} else if (!option.equals(5)) {
					flight.setReservedSeats(flight.getReservedSeats() + 1);
					executed = fdao.updateFlight(flight);
					booking.getFlights().add(flight);
					BookingDAO bdao = new BookingDAO(conn);
					executed = bdao.addBooking(booking);
				}
			}
			conn.commit();
		} catch (SQLException | ClassNotFoundException e) {
			conn.rollback();
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		return executed;
	}

	public Boolean cancelBooking(Index app) throws SQLException {
		Boolean executed = Boolean.FALSE;
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		Connection conn = null;
		try {
			conn = app.getConnUtil().getConnection();
			BookingDAO bdao = new BookingDAO(conn);
			List<Booking> bookings = bdao.getActiveBookings();
			List<Booking> userBookings = new ArrayList<>();
			System.out.println("Your trips:");
			Integer counter = 1;
			for (Booking booking : bookings) {
				if (booking.getBookedBy().getUserId().equals(app.getUser().getUserId())
						|| booking.getPassenger().getUserId().equals(app.getUser().getUserId())) {
					userBookings.add(booking);
					System.out.println(counter + ") " + booking.getFlights().get(0).getRoute().getOrigin().getCityName()
							+ " to " + booking.getFlights().get(0).getRoute().getDestination().getCityName());
					counter++;
				}

			}
			if (counter.equals(1)) {
				System.out.println("\tYou have no trips scheduled currently.");
			}
			System.out.println(counter + ") Quit to Previous");
			System.out.print("Selection: ");
			Integer option = scanner.nextInt();
			if (!option.equals(counter) & !option.equals(0)) {
				Booking booking = bookings.get(option - 1);
				booking.setIsActive(0);
				bdao.updateBooking(booking);
				FlightDAO fdao = new FlightDAO(conn);
				Flight flight = booking.getFlights().get(0);
				flight.setReservedSeats(flight.getReservedSeats() - 1);
				fdao.updateFlight(flight);
			}
			conn.commit();
			executed = Boolean.TRUE;
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			conn.rollback();
		} finally {
			conn.close();
		}
		return executed;
	}
}