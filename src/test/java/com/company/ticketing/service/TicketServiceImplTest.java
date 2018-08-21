package com.company.ticketing.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.company.ticketing.model.Seat;
import com.company.ticketing.model.SeatHold;
import com.company.ticketing.model.SeatHold.Status;

/**
 * Test for the ticketing service implementation.
 * 
 * @author daniel
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class TicketServiceImplTest {
	
	private final String email = "someone@whatever.com";

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
		
		// Hold the seats
		SeatHold seatHold = service.findAndHoldSeats(5, email);
		seatHold.setService(service);
		
		List<Seat> mySeats = seatHold.getSeats();
		
		// Reserve held seats
		String reservationId = service.reserveSeats(seatHold.getId(), seatHold.getCustomerEmail());
		assertNotNull(reservationId);
		assertTrue(reservationId.equals(SeatHold.RES_CODE_PREFIX + seatHold.getId()));

		
		// Make another reservation
		SeatHold sh2 = service.findAndHoldSeats(5, email);
		sh2.setId(1);
		sh2.holdSeats(1000);
		assertEquals(5, sh2.getSeats().size());
		
		boolean found6 = false;
		boolean found7 = false;
		boolean found8 = false;
		boolean found9 = false;
		boolean found10 = false;
		
		for(Seat s : sh2.getSeats()) {
			if(s.getRow() == 0 && s.getNum() == 2) {
				found6 = true;
			}
			else if(s.getRow() == 0 && s.getNum() == 8) {
				found7 = true;
			}
			else if(s.getRow() == 0 && s.getNum() == 1) {
				found8 = true;
			}
			else if(s.getRow() == 0 && s.getNum() == 9) {
				found9 = true;
			}
			else if(s.getRow() == 0 && s.getNum() == 0) {
				found10 = true;
			}
		}
		assertTrue(found6);
		assertTrue(found7);
		assertTrue(found8);
		assertTrue(found9);
		assertTrue(found10);
		
		reservationId = service.reserveSeats(sh2.getId(), email);
		assertEquals(SeatHold.RES_CODE_PREFIX + sh2.getId(), reservationId);
		assertFalse(sh2.getActive());
		for(Seat s : sh2.getSeats()) {
			assertEquals(email, s.getCustomerEmail());
			assertTrue(s.getReserved());
			assertFalse(s.getHold());
		}
	}
	
	@Test 
	public void test_holdSeat_oneRow() {
		TicketServiceImpl service = new TicketServiceImpl();
		service.setHoldSeatsTimeMillis(5000);
		
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
	public void test_holdSeat_andReserveFails() {
		TicketServiceImpl service = new TicketServiceImpl();
		service.setHoldSeatsTimeMillis(5000);
		
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
		
		// The seats received
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
			TimeUnit.SECONDS.sleep(6);
			assertTrue(!seatHold.getActive());
			
			for(Seat s : mySeats) {
				assertTrue(s.getAvailable());
				assertFalse(s.getHold());
				assertFalse(s.getReserved());
			}
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertFalse(seatHold.getActive());
		
		// After expiration, try to reserve the seats (fails)
		int seatHoldId = seatHold.getId();
		String resCode = service.reserveSeats(seatHoldId, email);
		assertNull(resCode);
	}
	
	@Test 
	public void test_holdSeat_andReserveSuccess() {
		TicketServiceImpl service = new TicketServiceImpl();
		service.setHoldSeatsTimeMillis(5000);
		
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
		
		// Wait a bit but still in "on hold" timeframe
		try {
			TimeUnit.SECONDS.sleep(2);
			assertTrue(seatHold.getActive());
			
			for(Seat s : mySeats) {
				assertTrue(s.getHold());
				assertFalse(s.getReserved());
			}
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		String reservationId = service.reserveSeats(seatHold.getId(), "someone@whatever.com");
		assertTrue(reservationId.equals(SeatHold.RES_CODE_PREFIX + seatHold.getId()));
		assertFalse(seatHold.getActive());
		for(Seat s : seatHold.getSeats()) {
			assertFalse(s.getHold());
			assertTrue(s.getReserved());
		}
	}
}
