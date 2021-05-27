/**
 * 
 */
package com.ss.utopia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ss.utopia.entity.Booking;
import com.ss.utopia.entity.Flight;
import com.ss.utopia.entity.User;

/**
 * Booking DAO handles CRUD operations and connections.
 * 
 * @author ahmed
 *
 */
public class BookingDAO extends MainDAO<Booking> {

	public BookingDAO(Connection conn) {
		super(conn);
	}

	public Boolean addBooking(Booking booking) throws SQLException, ClassNotFoundException {
		Boolean executed = Boolean.FALSE;
		save("INSERT INTO booking (`is_active`, `confirmation_code`) VALUES (?, ?);",
				new Object[] { booking.getIsActive(), booking.getConfirmationCode() });

		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM booking WHERE confirmation_code = '" + booking.getConfirmationCode() + "'");
		PreparedStatement pstmt = conn.prepareStatement(query.toString());
		ResultSet results = pstmt.executeQuery();
		results.next();
		booking.setBookingId(results.getInt("id"));
		save("INSERT INTO booking_payment (`booking_id`, `stripe_id`, `refunded`) VALUES (?, ?, ?);",
				new Object[] { booking.getBookingId(), booking.getStripeId(), booking.getRefunded() });
		if (booking.getBookedBy().getRole().equals(1) || booking.getBookedBy().getRole().equals(3)) {
			save("INSERT INTO booking_agent (`booking_id`, `agent_id`) VALUES (?, ?)",
					new Object[] { booking.getBookingId(), booking.getBookedBy().getUserId() });
			save("insert into passenger (`booking_id`, `given_name`, `family_name`, `dob`, `address`, `gender`) "
					+ "values(?, ?, ?, DATE_SUB(NOW(), interval 21 year)," + "'Unknown', 'Not Specified');",
					new Object[] { booking.getBookingId(), booking.getPassenger().getGivenName(),
							booking.getPassenger().getFamilyName() });
		} else if (booking.getBookedBy().getRole().equals(2)) {
			save("INSERT INTO booking_user (`booking_id`, `user_id`) VALUES (?, ?)",
					new Object[] { booking.getBookingId(), booking.getBookedBy().getUserId() });
			save("insert into passenger (`booking_id`, `given_name`, `family_name`, `dob`, `address`, `gender`) "
					+ "values(?, ?, ?, DATE_SUB(NOW(), interval 21 year)," + "'Unknown', 'Not Specified');",
					new Object[] { booking.getBookingId(), booking.getBookedBy().getGivenName(),
							booking.getBookedBy().getFamilyName() });
		} else {
			save("INSERT INTO booking_guest (`booking_id`, `contact_email`, `contact_phone`) " + "VALUES (?, ?, ?)",
					new Object[] { booking.getBookingId(), booking.getBookedBy().getEmail(),
							booking.getBookedBy().getPhoneNumber() });
			save("INSERT INTO passenger booking_id = ?, given_name = ?, family_name = ?,"
					+ "dob = DATE_SUB(NOW(), interval 21 year), gender = 'Not Specified', " + "address = 'Unknown'",
					new Object[] { booking.getBookingId(), booking.getBookedBy().getGivenName(),
							booking.getBookedBy().getFamilyName() });
		}
		for (Flight flight : booking.getFlights()) {
			save("INSERT INTO flight_bookings (`flight_id`, `booking_id`) VALUES (?, ?);",
					new Object[] { flight.getId(), booking.getBookingId() });
		}
		executed = Boolean.TRUE;
		return executed;
	}

	public List<Booking> getActiveBookings() throws SQLException, ClassNotFoundException {
		return read("SELECT * from booking where is_active = ?", new Object[] { 1 });
	}

	public List<Booking> getInactiveBookings() throws SQLException, ClassNotFoundException {
		return read("SELECT * from booking where is_active = ?", new Object[] { 0 });
	}

	public Boolean updateBooking(Booking booking) throws SQLException, ClassNotFoundException {
		Boolean executed = Boolean.FALSE;
		save("UPDATE booking SET is_active = ?, confirmation_code = ? WHERE id = ?;",
				new Object[] { booking.getIsActive(), booking.getConfirmationCode(), booking.getBookingId() });

		save("UPDATE booking_payment SET stripe_id = ?, refunded = ? WHERE booking_id = ?;",
				new Object[] { booking.getStripeId(), booking.getRefunded(), booking.getBookingId() });
		for (Flight flight : booking.getFlights()) {
			save("UPDATE flight_bookings SET flight_id = ? WHERE booking_id = ?;",
					new Object[] { flight.getId(), booking.getBookingId() });
		}
		executed = Boolean.TRUE;
		return executed;
	}

	public Boolean removeBooking(Booking booking) throws SQLException, ClassNotFoundException {
		Boolean executed = Boolean.FALSE;
		save("DELETE FROM booking WHERE id = ?", new Object[] { booking.getBookingId() });
		save("DELETE FROM booking_user WHERE booking_id = ?", new Object[] { booking.getBookingId() });
		save("DELETE FROM booking_payment WHERE booking_id = ?", new Object[] { booking.getBookingId() });
		save("DELETE FROM flight_bookings WHERE booking_id = ?", new Object[] { booking.getBookingId() });
		executed = Boolean.TRUE;
		return executed;
	}

	
	public List<Booking> extractData(ResultSet rs) throws ClassNotFoundException, SQLException {
		List<Booking> bookings = new ArrayList<>();
		UserDAO udao = new UserDAO(conn);
		while (rs.next()) {
			Booking booking = new Booking();
			booking.setBookingId(rs.getInt("id"));
			booking.setIsActive(rs.getInt("is_active"));
			booking.setConfirmationCode(rs.getString("confirmation_code"));

			StringBuilder query = new StringBuilder();
			query.append("SELECT * FROM booking_user where booking_id = " + booking.getBookingId());
			PreparedStatement pstmt = conn.prepareStatement(query.toString());
			ResultSet results = pstmt.executeQuery();

			while (results.next()) {
				Integer userId = results.getInt("user_id");
				User user = new User();
				user.setUserId(userId);
				user = udao.findUser(user);
				booking.setBookedBy(user);
			}

			query.setLength(0);
			query.append("SELECT * FROM booking_payment where booking_id = " + booking.getBookingId());
			pstmt = conn.prepareStatement(query.toString());
			results = pstmt.executeQuery();

			while (results.next()) {
				booking.setStripeId(results.getString("stripe_id"));
				booking.setRefunded(results.getInt("refunded"));
			}

			FlightDAO fdao = new FlightDAO(conn);
			List<Flight> allFlights = fdao.getFlights();

			query.setLength(0);
			query.append("SELECT * FROM flight_bookings where booking_id = " + booking.getBookingId());
			pstmt = conn.prepareStatement(query.toString());
			results = pstmt.executeQuery();
			List<Flight> flights = new ArrayList<>();
			while (results.next()) {
				for (Flight flight : allFlights) {
					if (flight.getId().equals(results.getInt("flight_id"))) {
						flights.add(flight);
					}
				}
			}
			booking.setFlights(flights);
			bookings.add(booking);
		}

		return bookings;
	}
}