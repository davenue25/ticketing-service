package com.company.ticketing.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.company.ticketing.service.TicketService;
import com.company.ticketing.service.TicketServiceImpl;


 /** 
 * Tests for seat hold.
 * @author daniel
 * August 2018 
 */ 

@RunWith(MockitoJUnitRunner.class) 
public class SeatHoldTest {

    @Test 
    public void testModel() { 
        int id = 481;
        TicketService service = new TicketServiceImpl();
        
        String customerEmail = "someone@whatever.com";
        boolean active = true;
        

        SeatHold seatHold = new SeatHold(service);
        Seat seat1 = new Seat(0,1);
        Seat seat2 = new Seat(0,0);
        
        seatHold.setId(id);
        seatHold.setService(service);
        seatHold.setCustomerEmail(customerEmail);
        seatHold.setActive(active);
        seatHold.addSeat(seat1);
        seatHold.addSeat(seat2);

        assertEquals(id, seatHold.getId());
        assertEquals(service, seatHold.getService());
        assertEquals(customerEmail, seatHold.getCustomerEmail());
        assertEquals(active, seatHold.getActive());
        assertEquals(seatHold.hashCode(), seatHold.hashCode());
        assertTrue(seatHold.equals(seatHold));
        assertEquals(seatHold.toString(), seatHold.toString());
        
        List<Seat> seats = seatHold.getSeats();
        boolean foundSeat1 = false;
        boolean foundSeat2 = false;
        for(Seat s : seats) {
        	if(s == seat1) {
        		foundSeat1 = true;
        	}
        	else if(s == seat2) {
        		foundSeat2 = true;
        	}
        }
        assertTrue(foundSeat1);
        assertTrue(foundSeat2);
        
        // Hold the seats
        seatHold.holdSeats(500);
    }
}
