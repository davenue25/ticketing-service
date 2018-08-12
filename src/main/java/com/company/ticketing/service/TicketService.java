package com.company.ticketing.service;

import java.util.List;

import com.company.ticketing.model.SeatHold;

public interface TicketService {
	
	public static final String MSG_SH_INVALID_ID = "SH_INVALID_ID";
	
	public static final String MSG_SH_RESERVED_CONFIRMATION_PREFIX = "SH_CONFIRMATION_NUMBER-";
	
	public static final String MSG_SH_NOT_ACTIVE = "SH_NOT_ACTIVE";
	
	public static final String MSG_SH_BAD_SEATS = "SH_BAD_SEATS";
	
	/**
	 * The number of seats in the venue that are neither held nor reserved
	 *
	 * @return the number of tickets available in the venue
	 */
	int numSeatsAvailable();

	/**
	 * Find and hold the best available seats for a customer
	 *
	 * @param numSeats
	 *            the number of seats to find and hold
	 * @param customerEmail
	 *            unique identifier for the customer
	 * @return a SeatHold object identifying the specific seats and related
	 *         information
	 */
	SeatHold findAndHoldSeats(int numSeats, String customerEmail);

	/**
	 * Commit seats held for a specific customer
	 *
	 * @param seatHoldId
	 *            the seat hold identifier
	 * @param customerEmail
	 *            the email address of the customer to which the seat hold is
	 *            assigned
	 * @return a reservation confirmation code
	 */
	String reserveSeats(int seatHoldId, String customerEmail);
	
	void initializeVenueSize(List<Integer> venueSeats);
	
	void cancelSeatHold(SeatHold seatHold);
	
	int getTotalSeats();
}