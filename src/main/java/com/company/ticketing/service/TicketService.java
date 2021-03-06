package com.company.ticketing.service;

import java.util.List;

import com.company.ticketing.model.SeatHold;

public interface TicketService {
	
	/** An invalid seat holder id was specified */
	public static final String MSG_SH_INVALID_ID = "SH_INVALID_ID";
	
	public static final String MSG_CANNOT_RESERVE_SEATHOLD = "SH_CANNOT_RESERVE";
	
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
	
	/**
	 * Initialize the venue seats. Tell the service how many rows
	 * and how many seats per row.
	 * 
	 * Each integer in the list represents ONE row.
	 * The integer value represents the number of seats for that row.
	 * 
	 * @param venueSeats
	 */
	void initializeVenueSize(List<Integer> venueSeats);
	
	/**
	 * Cancel a seat hold.
	 * @param seatHold
	 */
	void cancelSeatHold(SeatHold seatHold);
	
	/**
	 * Get total number of seats in the venue.
	 * @return
	 */
	int getTotalSeats();
}