package com.company.ticketing.model;

/**
 * A specific seat in the venue.
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
	}
	
	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}


	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		if(row < 0) {
			throw new IllegalArgumentException("Row cannot be less than zero");
		}
		this.row = row;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		if(num < 0) {
			throw new IllegalArgumentException("Seat number cannot be less than zero");
		}
		this.num = num;
	}
	public boolean getReserved() {
		return reserved;
	}
	public void setReserved(boolean isReserved) {
		this.reserved = isReserved;
	}
	public boolean getHold() {
		return onHold;
	}
	public void setHold(boolean onHold) {
		this.onHold = onHold;
	}
	
	public boolean getAvailable() {
		return !reserved && !onHold;
	}
}
