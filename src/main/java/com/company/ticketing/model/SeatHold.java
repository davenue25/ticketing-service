package com.company.ticketing.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.company.ticketing.service.TicketService;

/**
 * The seat hold represents a group of seats that are available
 * for being put "on hold" or to be reserved. The "on hold" lasts
 * for a specified amount of time. After that time expires, they
 * are no longer "on hold" and this seat hold object is not active.
 * 
 * @author daniel
 *
 */
public class SeatHold extends BaseModel {

	private List<Seat> seatList = new ArrayList<>();
	
	// Is this seat hold active?
	private boolean active = false;
	
	// Were seats held?
	private boolean holdSeatsFlag = false;
	
	private TicketService service;
	
	private String customerEmail;
	
	// Is it being reserved 
	private boolean reserving = false;
	
	public SeatHold(TicketService service) {
		this.service = service;
	}
	
	public TicketService getService() {
		return service;
	}

	public void setService(TicketService service) {
		this.service = service;
	}

	private int id;
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}
	
	public String getCustomerEmail() {
		return customerEmail;
	}

	/**
	 * Add a seat to this seat holder. After all seats are added,
	 * call {@link #holdSeats(long)}
	 * @param seat
	 */
	public void addSeat(Seat seat) {
		if(seat != null && !seat.getReserved() && !seat.getHold()) {
			seat.setHold(true);
			seatList.add(seat);
		}
	}
	
	public void holdSeats(long seatsHoldTimeMillis) {
		if(seatList != null && seatList.size() > 0) {
			for(Seat seat : seatList) {
				seat.setHold(true);
			}
			
			final SeatHold seatHold = this;
			
			Timer timer = new Timer();
			TimerTask task = new TimerTask() {
				public void run() {
					if(!reserving) {
						service.cancelSeatHold(seatHold); 
					}
				}
			};
			timer.schedule(task, seatsHoldTimeMillis);
			active = true;
			holdSeatsFlag = true;
		}
	}
	
	public void cancelHold() {
		active = false;
		service.cancelSeatHold(this);
	}
	
	/**
	 * Reserve seats that are in this seat hold object.
	 * 
	 * You must call {@link #holdSeats(long)} first before calling this method.
	 * 
	 * If the seats are still "on hold" (seat hold active = true), calling this method will reserve those
	 * seats. 
	 * 
	 * If they are not "on hold" anymore (seat hold active = false), you will not be able to reserve those
	 * seats.
	 * 
	 * @return true if reserved, or false if not reserved
	 */
	public boolean reserveSeats() {
		reserving = true;
		
		if(!holdSeatsFlag) {
			return false;
		}
		
		boolean reservedFlag = false;
		
		if(active) {
			if(seatList != null && seatList.size() > 0) {
				for(Seat seat : seatList) {
					seat.setReserved(true);
					seat.setHold(false);
					seat.setCustomerEmail(customerEmail);
				}
				reservedFlag = true;
			}
		}
		
		reserving = false;
		
		setActive(false);
		
		return reservedFlag;
	}
	
	public int totalSeats() {
		return seatList.size();
	}
	
	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public List<Seat> getSeats() {
		return seatList;
	}
}
