package com.company.ticketing.model;

/**
 * A specific seat in the venue.
 * A seat is situated in a row and given a seat number.
 * 
 * @author daniel
 *
 */
public class Seat extends BaseModel {

	// Seat row
	private int row = 0;
	
	// Seat number
	private int num = 0;
	
	// Seat reserved
	private boolean reserved = false;
	
	// Seat on hold
	private boolean onHold   = false;
	
	private String customerEmail = null;
	
	public Seat(int row, int num) {
		this.row = row;
		this.num = num;
		if(row < 0) {
			throw new IllegalArgumentException("Seat row cannot be less than zero");
		}
		else if(num < 0) {
			throw new IllegalArgumentException("Seat number cannot be less than zero");
		}
	}
	
	/**
	 * The customer email who reserved this seat.
	 * @return
	 */
	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	/**
	 * Get the seat row.
	 * @return
	 */
	public int getRow() {
		return row;
	}
	
	/**
	 * Get seat number.
	 * @return
	 */
	public int getNum() {
		return num;
	}
	
	/**
	 * Is seat reserved.
	 * @return
	 */
	public boolean getReserved() {
		return reserved;
	}
	public void setReserved(boolean isReserved) {
		this.reserved = isReserved;
	}
	
	/**
	 * Is seat on hold.
	 * @return
	 */
	public boolean getHold() {
		return onHold;
	}
	public void setHold(boolean onHold) {
		this.onHold = onHold;
	}
	
	/**
	 * Is seat is available: if not on hold and not reserved.
	 * @return
	 */
	public boolean getAvailable() {
		return !reserved && !onHold;
	}
}
