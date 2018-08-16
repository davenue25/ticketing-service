package com.company.ticketing.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.company.ticketing.model.Seat;
import com.company.ticketing.model.SeatHold;

@RunWith(MockitoJUnitRunner.class)
public class TicketServiceImplTest {
	
	private String email = "daniel@whatever.com";

	@Test
	public void test_initalizeVenueSize_invalidSizes() {
		TicketService service = new TicketServiceImpl();
		
		List<Integer> venueSeats = null;
		
		boolean exceptionFlag = false;
		try {
			service.initializeVenueSize(venueSeats);
		}
		catch(Exception ex) {
			exceptionFlag = true;
			assertTrue(ex instanceof IllegalArgumentException);
		}
		assertTrue(exceptionFlag);
		
		/////
		
		venueSeats = new ArrayList<>();
		venueSeats.add(0);
		venueSeats.add(0);
		service = new TicketServiceImpl();
		exceptionFlag = false;
		try {
			service.initializeVenueSize(venueSeats);
		}
		catch(Exception ex) {
			exceptionFlag = true;
			assertTrue(ex instanceof IllegalArgumentException);
		}
		assertTrue(exceptionFlag);
	}
	
	@Test
	public void test_initalizeVenueSize_invalidInit() {
		TicketService service = new TicketServiceImpl();
		
		List<Integer> venueSeats = new ArrayList<>();
		venueSeats.add(1);
		venueSeats.add(2);
		
		boolean exceptionFlag = false;
		try {
			service.initializeVenueSize(venueSeats);
		}
		catch(Exception ex) {
			exceptionFlag = true;
		}
		assertFalse(exceptionFlag);
		
		///// Init a second time (invalid)
		try {
			service.initializeVenueSize(venueSeats);
		}
		catch(Exception ex) {
			exceptionFlag = true;
			assertTrue(ex instanceof UnsupportedOperationException);
		}
		assertTrue(exceptionFlag);
	}
	
	@Test
	public void test_initalizeVenueSize_success() {
		TicketService service = new TicketServiceImpl();
		
		List<Integer> venueSeats = new ArrayList<>();
		venueSeats.add(1);
		venueSeats.add(2);
		
		service.initializeVenueSize(venueSeats);
		
		int totalSeats = service.getTotalSeats();
		assertEquals(3, totalSeats);
	}
	
	@Test
	public void test_reserveSeat() {
		TicketServiceImpl service = new TicketServiceImpl();
		service.setHoldSeatsTimeMillis(3000);
		
		List<Integer> venueSeats = new ArrayList<>();
		venueSeats.add(10);
		
		service.initializeVenueSize(venueSeats);
		
		SeatHold seatHold = service.findAndHoldSeats(5, email);
		seatHold.setService(service);
		
		List<Seat> mySeats = seatHold.getSeats();
		
		// Success
		String reservationId = service.reserveSeats(seatHold.getId(), seatHold.getCustomerEmail());
		assertNotNull(reservationId);
		assertTrue(reservationId.equals(service.MSG_SH_RESERVED_CONFIRMATION_PREFIX + seatHold.getId()));

		// Failure - invalid seat holder id
		int invalidSeatHoldId = 1051;
		reservationId = service.reserveSeats(invalidSeatHoldId, "someone@whatever");
		assertTrue(reservationId.equals(service.MSG_SH_INVALID_ID));
		
		// Failure - bad seat holder
		seatHold.setActive(false);
		reservationId = service.reserveSeats(seatHold.getId(), "someone@whatever");
		assertTrue(reservationId.equals(service.MSG_SH_NOT_ACTIVE));
		
		seatHold.setActive(true);
		seatHold.getSeats().clear();
		reservationId = service.reserveSeats(seatHold.getId(), "someone@whatever");
		assertTrue(reservationId.equals(service.MSG_SH_BAD_SEATS));
		
	}
	
	@Test 
	public void test_holdSeat_oneRow() {
		TicketServiceImpl service = new TicketServiceImpl();
		service.setHoldSeatsTimeMillis(3000);
		
		// Even seats in row
		List<Integer> venueSeats = new ArrayList<>();
		venueSeats.add(10);
		
		service.initializeVenueSize(venueSeats);
		
		SeatHold seatHold = service.findAndHoldSeats(5, email);
		seatHold.setService(service);
		
		List<Seat> mySeats = seatHold.getSeats();
		assertEquals(5, mySeats.size());
		
		boolean foundSeat1 = false;
		boolean foundSeat2 = false;
		boolean foundSeat3 = false;
		boolean foundSeat4 = false;
		boolean foundSeat5 = false;
		for(Seat s : mySeats) {
			if(s.getRow() == 0 && s.getNum() == 5) {
				foundSeat1 = true;
			}
			else if(s.getRow() == 0 && s.getNum() == 4) {
				foundSeat2 = true;
			}
			else if(s.getRow() == 0 && s.getNum() == 6) {
				foundSeat3 = true;
			}
			else if(s.getRow() == 0 && s.getNum() == 3) {
				foundSeat4 = true;
			}
			else if(s.getRow() == 0 && s.getNum() == 7) {
				foundSeat5 = true;
			}
			assertTrue(s.getHold());
			assertFalse(s.getAvailable());
			assertFalse(s.getReserved());
			
			System.out.println("SEAT FOUND: " + s);
		}
		assertEquals(email, seatHold.getCustomerEmail());
		assertTrue(foundSeat1);
		assertTrue(foundSeat2);
		assertTrue(foundSeat3);
		assertTrue(foundSeat4);
		assertTrue(foundSeat5);
		assertTrue(seatHold.getActive());
		
		
		// Odd seats in row
		service = new TicketServiceImpl();
		venueSeats = new ArrayList<>();
		venueSeats.add(11);
		
		service.initializeVenueSize(venueSeats);
		
		seatHold = service.findAndHoldSeats(5, email);
		seatHold.setService(service);
		
		mySeats = seatHold.getSeats();
		assertEquals(5, mySeats.size());
	}
	
	@Test 
	public void test_holdSeat_twoRows() {
		TicketServiceImpl service = new TicketServiceImpl();
		service.setHoldSeatsTimeMillis(3000);
		
		// Even row
		List<Integer> venueSeats = new ArrayList<>();
		venueSeats.add(2);
		venueSeats.add(6);
		
		service.initializeVenueSize(venueSeats);
		
		SeatHold seatHold = service.findAndHoldSeats(5, email);
		seatHold.setService(service);
		
		List<Seat> mySeats = seatHold.getSeats();
		assertEquals(5, mySeats.size());
		
		boolean foundSeat1 = false;
		boolean foundSeat2 = false;
		boolean foundSeat3 = false;
		boolean foundSeat4 = false;
		boolean foundSeat5 = false;
		for(Seat s : mySeats) {
			if(s.getRow() == 0 && s.getNum() == 1) {
				foundSeat1 = true;
			}
			else if(s.getRow() == 0 && s.getNum() == 0) {
				foundSeat2 = true;
			}
			else if(s.getRow() == 1 && s.getNum() == 3) {
				foundSeat3 = true;
			}
			else if(s.getRow() == 1 && s.getNum() == 2) {
				foundSeat4 = true;
			}
			else if(s.getRow() == 1 && s.getNum() == 4) {
				foundSeat5 = true;
			}
			assertTrue(s.getHold());
			assertFalse(s.getAvailable());
			assertFalse(s.getReserved());
			
			System.out.println("SEAT FOUND: " + s);
		}
		assertEquals(email, seatHold.getCustomerEmail());
		assertTrue(foundSeat1);
		assertTrue(foundSeat2);
		assertTrue(foundSeat3);
		assertTrue(foundSeat4);
		assertTrue(foundSeat5);
		assertTrue(seatHold.getActive());
	}
	
	@Test 
	public void test_holdSeat_multiRows() {
		TicketServiceImpl service = new TicketServiceImpl();
		service.setHoldSeatsTimeMillis(3000);
		
		// Multi-rows of seats
		List<Integer> venueSeats = new ArrayList<>();
		venueSeats.add(1);
		venueSeats.add(2);
		venueSeats.add(0);
		venueSeats.add(10);
		
		service.initializeVenueSize(venueSeats);
		
		SeatHold seatHold = service.findAndHoldSeats(5, email);
		seatHold.setService(service);
		
		List<Seat> mySeats = seatHold.getSeats();
		assertEquals(5, mySeats.size());
		
		boolean foundSeat1 = false;
		boolean foundSeat2 = false;
		boolean foundSeat3 = false;
		boolean foundSeat4 = false;
		boolean foundSeat5 = false;
		for(Seat s : mySeats) {
			if(s.getRow() == 0 && s.getNum() == 0) {
				foundSeat1 = true;
			}
			else if(s.getRow() == 1 && s.getNum() == 1) {
				foundSeat2 = true;
			}
			else if(s.getRow() == 1 && s.getNum() == 0) {
				foundSeat3 = true;
			}
			else if(s.getRow() == 3 && s.getNum() == 5) {
				foundSeat4 = true;
			}
			else if(s.getRow() == 3 && s.getNum() == 4) {
				foundSeat5 = true;
			}
			assertTrue(s.getHold());
			assertFalse(s.getAvailable());
			assertFalse(s.getReserved());
			
			System.out.println("SEAT FOUND: " + s);
		}
		assertEquals(email, seatHold.getCustomerEmail());
		assertTrue(foundSeat1);
		assertTrue(foundSeat2);
		assertTrue(foundSeat3);
		assertTrue(foundSeat4);
		assertTrue(foundSeat5);
		assertTrue(seatHold.getActive());
		
		
		// Wait until "on hold" expires
		try {
			System.out.println("waiting...");
			TimeUnit.SECONDS.sleep(5);
			System.out.println("waiting...done");
			assertTrue(!seatHold.getActive());
			
			for(Seat s : mySeats) {
				assertTrue(!s.getHold());
				assertTrue(!s.getReserved());
			}
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// After expiration, try to reserve the seats (fails)
		int seatHoldId = seatHold.getId();
		String confirmationId = service.reserveSeats(seatHoldId, email);
		assertTrue(TicketService.MSG_SH_INVALID_ID.equals(confirmationId));
	}
}
