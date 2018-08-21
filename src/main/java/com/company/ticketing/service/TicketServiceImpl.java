package com.company.ticketing.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.validator.routines.EmailValidator;

import com.company.ticketing.model.Seat;
import com.company.ticketing.model.SeatHold;

/**
 * Ticket service implementation
 * @author daniel
 *
 */
public class TicketServiceImpl implements TicketService {
	
	private long seatsHoldTimeMillis = 60000l; // default one minute
	
	/**
	 * Milliseconds to hold the a seat for.
	 * @param millis
	 */
	public void setHoldSeatsTimeMillis(long millis) {
		this.seatsHoldTimeMillis = millis;
	}
	
	public long getHoldSeatsTimeMillis() {
		return seatsHoldTimeMillis;
	}

	private ArrayList<ArrayList<Seat>> venueSeats = new ArrayList<ArrayList<Seat>>();
	
	private boolean venueSeatsInitialized = false;
	
	// Primitive counter/id generator for this programming challenge
	private int seatHolderIdCounter = 0;
	
	// Hold all seathold objects: key = seatHoldId, value = seat hold object
	private Map<Integer, SeatHold> seatHoldMap = new HashMap<>();
	
	
	/**
	 * Define the venue seating dimensions.
	 * Given an list, every integer in the list represents the number of 
	 * seats for a given row (the current list index).
	 * 
	 * For example, a list with the following values: 5, 7, 9
	 * means the venue has three rows: 1st row = 5 seats, 2nd row = 7 seats, and 3rd row = 9 seats
	 * 
	 * Cannot be initialized more than once or an {@link UnsupportedOperationException} will be thrown.
	 * If the parameter is null or has no entries, an {@link IllegalArgumentException} will be thrown.
	 * 
	 * @param List<Integer> each entry represents the number of seats for the current row (index)
	 * @exception UnsupportedOperationException if initialized more than once, or {@link IllegalArgumentException}
	 * if parameter is null or has zero rows (empty list passed in) or if there zero seats.
	 */
	@Override 
	public void initializeVenueSize(List<Integer> rowsAndColumns) {
		if(venueSeatsInitialized) {
			throw new UnsupportedOperationException("Cannot initialize venue size more than once");
		}
		
		if(rowsAndColumns == null) {
			throw new IllegalArgumentException("Cannot have a null venue size");
		}
		else if(rowsAndColumns.size() == 0) {
			throw new IllegalArgumentException("Venue must have at least one row");
		}
		
		int totalSeats = 0;
		for(Integer seatSize : rowsAndColumns) {
			totalSeats += seatSize;
		}
		if(totalSeats == 0) {
			throw new IllegalArgumentException("Venue must have at least one seat");
		}
		
		int currentRowIndex = 0;
		
		
		for(Integer seatSize : rowsAndColumns) {
			ArrayList<Seat> currentRow = new ArrayList<>();
			
			for(int i = 0; i < seatSize; i++) {
				Seat seat = new Seat(currentRowIndex, i);
				currentRow.add(seat);
				
				totalSeats++;
			}
			venueSeats.add(currentRow);
			currentRowIndex++;
		}
		
		venueSeatsInitialized = true;
	}
	
	@Override
	public int numSeatsAvailable() {
		int totalSeats = 0;
		for(ArrayList<Seat> rows : venueSeats) {
			for(Seat seat : rows) {
				if(seat.getAvailable()) {
					totalSeats++;
				}
			}
		}
		return totalSeats;
	}
	
	@Override
	public int getTotalSeats() {
		if(venueSeats == null) {
			return 0;
		}
		int totalSeats = 0;
		for(List<Seat> currentRow : venueSeats) {
			totalSeats += (currentRow == null ? 0 : currentRow.size());
		}
		return totalSeats;
	}
	
	/**
	 * Is the seat a valid that can be found in the venue.
	 * 
	 * @param currentRow the current row in the venue 
	 * @param currentRowIndex the current row index 
	 * @param currentRowSize the current row size
	 * @param totalRows total rows
	 * @param currentSeatIndex current seat index
	 * @return
	 */
	public boolean isValidSeatLocation(List<Seat> currentRow, int currentRowIndex, int currentRowSize, int totalRows, 
		int currentSeatIndex) {
		if(currentRowIndex > totalRows || 
			currentRowIndex < 0 || 
			currentSeatIndex > currentRowSize ||
			currentSeatIndex < 0) {
			return false;
		}
		return true;
	}

	@Override
	public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
		
		// Invalid number of seats
		if(numSeats <= 0) {
			throw new IllegalArgumentException("Number of seats must be greater than 0");
		}
		
		// Don't have that many seats available
		int availableSeats = numSeatsAvailable();
		if(numSeats > availableSeats) {
			throw new RuntimeException("Number of seats requested not available. Requested " 
				+ numSeats + " but have " + availableSeats);
		}
		
		// Email is invalid
		EmailValidator ev = EmailValidator.getInstance();
		boolean emailValid = ev.isValid(customerEmail);
		if(!emailValid) {
			throw new RuntimeException("Invalid email: " + customerEmail);
		}
		
		SeatHold seatHold = new SeatHold(this);
		seatHold.setId(seatHolderIdCounter);
		seatHolderIdCounter++;
		
		seatHold.setCustomerEmail(customerEmail);
		
		// Find the best seat in the venue
		// Assumption: best seat equals closest to stage, and in the middle of the row, then spreading out to sides
		int foundSeats = 0;
		
		int op = 0; // 0 = middle, -1 go left of middle, 1 go right of middle
		int goLeftCount = 0;
		int goRightCount = 0;
		boolean firstIndexReached = false;
		boolean lastIndexReached = false;
		for(List<Seat> cr : venueSeats) {
			
			int crSize = cr.size();
			if(crSize == 0) {
				continue;
			}
			int middleIndex = crSize / 2;
			
			while((foundSeats < numSeats)) {
				
				if(firstIndexReached && lastIndexReached) {
					firstIndexReached = false;
					lastIndexReached = false;
					op = 0;
					goLeftCount = 0;
					goRightCount = 0;
					break;
				}
				
				int index = middleIndex;
				
				if(op == 0) {
					op = -1;
				}
				else if(op == -1) {
					if(!firstIndexReached) {
						goLeftCount++;
						index = middleIndex - goLeftCount;
						op = 1;
					}
					else {
						goRightCount++;
						index = middleIndex + goRightCount;
					}
				}
				else if(op == 1) {
					if(!lastIndexReached) {
						goRightCount++;
						index = middleIndex + goRightCount;
						op = -1;
					}
					else {
						goLeftCount++;
						index = middleIndex - goLeftCount;
					}
				}
				
				if(index == cr.size() -1) {
					lastIndexReached = true;
				}
				if(index == 0) {
					firstIndexReached = true;
				}
				
				Seat seat = cr.get(index);
				if(seat.getAvailable()) {
					seatHold.addSeat(seat);
					foundSeats++;
				}
				if(index == 0) {
					firstIndexReached = true;
				}
				else if(index >= cr.size() -1) {
					lastIndexReached = true;
				}
				
				if(foundSeats != numSeats) {
					continue;
				}
				else {
					break;
				}
			}
		}
		
		// Found seats. Now hold them.
		if(seatHold.totalSeats() > 0) {
			seatHold.holdSeats(seatsHoldTimeMillis);
			seatHoldMap.put(seatHold.getId(), seatHold);
			int x = 0;
		}
		
		return seatHold;
	}

	/**
	 * Reserve the seats in a seat hold.
	 * 
	 * The seat hold must be active, and must reserve one or more seats.
	 * 
	 * If the seat hold cannot be found be seatHoldId, it cannot be reserved.
	 * 
	 * {@value TicketService#MSG_SH_BAD_SEATS}
	 * 
	 * @param int seatHoldId the seatHold id to reserve
	 * @param String customerEmail the customer email for reserve it under
	 * @return {@link TicketService#MSG_SH_RESERVED_CONFIRMATION_PREFIX} and confirmation id if reserved;
	 * 		   {@link TicketService#MSG_SH_NOT_ACTIVE} if seat hold is not active (expired hold);
	 * 		   {@link TicketService#MSG_BAD_SEATS} if null or zero seats;
	 * 	       {@link TicketService#MSG_SH_INVALID_ID} if seat hold id is not found;
	 * 
	 */
	@Override
	public String reserveSeats(int seatHoldId, String customerEmail) {
		SeatHold seatHold = seatHoldMap.get(seatHoldId);
		if(seatHold != null) {
			boolean reserved = seatHold.reserveSeats();
			if(reserved) {
				return seatHold.getResCode();
			}
		}
		return null;
	}
	
	/** 
	 * Find a seat hold by id
	 *  
	 * @param seatHoldId
	 * @return seat hold or null if not found
	 */
	public SeatHold getSeatHold(int seatHoldId) {
		return seatHoldMap.get(seatHoldId);
	}

	@Override
	public void cancelSeatHold(SeatHold seatHold) {
		if(seatHold != null) {
			seatHold.setActive(false);
			seatHoldMap.remove(seatHold.getId());
			
			List<Seat> seats = seatHold.getSeats();
			int totalRows = venueSeats != null ? venueSeats.size() : 0;
			if(seats != null && totalRows > 0) {
				for(Seat seat : seats) {
					seat.setHold(false);
				}
			}
		}
	}
}
