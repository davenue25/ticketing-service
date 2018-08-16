package com.company.ticketing.model;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

 /** 
 * Test for seat model.
 * 
 * @author daniel
 * August 2018
 */ 

@RunWith(MockitoJUnitRunner.class) 
public class SeatTest {

    @Test 
    public void test_Model() { 
        boolean reserved = true;
        int num = 173;
        boolean hold = true;
        int row = 471;
        String email = "someone@whatever.com";

        Seat model = new Seat(row, num);

        model.setReserved(reserved);
        model.setNum(num);
        model.setHold(hold);
        model.setRow(row);
        model.setCustomerEmail(email);

        assertEquals(false, model.getAvailable());
        assertEquals(reserved, model.getReserved());
        assertEquals(num, model.getNum());
        assertEquals(hold, model.getHold());
        assertEquals(row, model.getRow());
        assertEquals(email, model.getCustomerEmail());
        assertEquals(model.hashCode(), model.hashCode());
        assertTrue(model.equals(model));
        assertEquals(model.toString(), model.toString());
        
        assertFalse(model.getAvailable());
        
        model.setReserved(false);
        model.setHold(false);
        assertTrue(model.getAvailable());
        
        model.setReserved(true);
        assertFalse(model.getAvailable());
        
        model.setReserved(false);
        model.setHold(true);
        assertFalse(model.getAvailable());
        
        
    }
}
