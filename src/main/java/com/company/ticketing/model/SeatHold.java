package com.company.ticketing.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;

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
public class SeatHold {

	private List<Seat> listSeats = new ArrayList<>();
	
	private boolean active = false;
	
	// Was "on hold" functionality performed?
	private boolean holdSeatsFlag = false;
	
	
	private TicketService service;
	
	@Autowired
	public TicketService getService() {
		return service;
	}

	public void setService(TicketService service) {
		this.service = service;
	}

	private String customerEmail;
	
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

	public void addSeat(Seat seat) {
		if(seat != null && !seat.getReserved() && !seat.getHold()) {
			seat.setHold(true);
			listSeats.add(seat);
		}
	}
	
	public void holdSeats(long seatsHoldTimeMillis) {
		if(listSeats != null && listSeats.size() > 0) {
			for(Seat seat : listSeats) {
				seat.setHold(true);
			}
			
			final SeatHold seatHold = this;
			
			Timer timer = new Timer();
			TimerTask task = new TimerTask() {
				public void run() {
					System.out.println("SeatHold.holdSeats() service = "  + service);
					service.cancelSeatHold(seatHold); 
				}
			};
			timer.schedule(task, seatsHoldTimeMillis);
			active = true;
			holdSeatsFlag = true;
		}
	}
	
//	public void holdSeats(long seatsHoldTimeMillis) {
//		active = true;
//		onHoldFlag = true;
//		if(listSeats != null) {
//			for(Seat seat : listSeats) {
//				seat.setHold(true);
//			}
//			
//			final SeatHold seatHold = this;
//			
//			Timer timer = new Timer();
//			TimerTask task = new TimerTask() {
//				public void run() {
//					service.cancelSeatHold(seatHold);
//				}
//			};
//			timer.schedule(task, seatsHoldTimeMillis);
//		}
//	}
	
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
		if(!holdSeatsFlag) {
			return false;
		}
		
		boolean reservedFlag = false;
		
		if(active) {
			if(listSeats != null && listSeats.size() > 0) {
				for(Seat seat : listSeats) {
					seat.setReserved(true);
				}
				reservedFlag = true;
			}
		}
		return reservedFlag;
	}
	
	public int totalSeats() {
		return listSeats.size();
	}
	
	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public List<Seat> getSeats() {
		return listSeats;
	}
	
	public void addListener(TicketService service) {
		this.service = service;
	}
	
	public String toString() {
		String x = null;
		for(Seat s: listSeats) {
			x += s;
		}
		return x;
	}
}
