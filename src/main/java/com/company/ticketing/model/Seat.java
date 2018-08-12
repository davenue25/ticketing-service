package com.company.ticketing.model;

import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Value;

/**
 * A specific seat in the venue.
 * @author daniel
 *
 */
public class Seat {

	private int row = 0;
	private int num = 0;
	
	private boolean reserved = false;
	private boolean onHold   = false;
	
	@Value("${seat.reservationMaxSeconds:60}")
	private long reserveTime    = 0;
	
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int col) {
		this.num = col;
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
//		if(!reserved && onHold) {
//			Timer timer = new Timer();
//			TimerTask task = new TimerTask() {
//				public void run() {
//					setHold(false);
//				}
//			};
//			timer.schedule(task, reserveTime);
//		}
	}
	
	@Override
	public String toString() {
		return "Seat(" + row + "," + num + ") reserved = " + reserved + ", onHold = " + onHold + ", available = " + getAvailable();
	}
	
	public boolean getAvailable() {
		return !reserved && !onHold;
	}
}
