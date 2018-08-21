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
 * Add the seats to this object. Once the call to the hold method
 * is called, this seat hold become active and the seats become held.
 * 
 * @author daniel
 *
 */
public class SeatHold extends BaseModel {
	
	/** The reservation confirmation code */
	public static final String RES_CODE_PREFIX = "RESERVATION-CODE-";
	
	/** Validation statuses */
	public enum Status {
		NOT_ACTIVE("Seat hold not active"),
		BAD_SEATS("Null or empty seats");
		
		private String text;
		Status(String text) {
			this.text = text;
		}
		
		public String text() {
			return text;
		}
	}
	/** List of error messages generating during validation */
	private List<Status> statusList = new ArrayList<>();

	/** The list of seats in this seat hold */
	private List<Seat> seatList = new ArrayList<>();
	
	
	
	public String resCode = null;
	
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
	
	/**
	 * The reservation confirmation code. If not reserved, this returns null.
	 * @return
	 */
	public String getResCode() {
		return resCode;
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
	
	/**
	 * Hold the seats after you added all seats you want to hold to this object.
	 * 
	 * @param seatsHoldTimeMillis how long does the hold last for?
	 */
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
	
	/**
	 * Cancel the hold for all seats.
	 */
	public void cancelHold() {
		active = false;
		service.cancelSeatHold(this);
	}
	
	/**
	 * Validate this seat holder object
	 * 
	 * @return List of Status messages if something did not validate or empty list if everything is OK
	 */
	public List<Status> validate() {
		
		statusList.clear();
		
		if(!getActive()) {
			statusList.add(Status.NOT_ACTIVE);
		}
		else if(getSeats() == null || getSeats().size() == 0) {
			statusList.add(Status.BAD_SEATS);
		};
		
		return statusList;
	}
	
	/**
	 * Get the list of validation status messages. These messages are
	 * created after calling the {@link #validate()} method.
	 * @return
	 */
	public List<Status> getValidationMessages() {
		return statusList;
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
		else if(validate().size() > 0) {
			return false;
		}
		
		
		boolean reservedFlag = false;
		
		reserving = true;
		
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
		
		resCode = RES_CODE_PREFIX + getId();
		
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
