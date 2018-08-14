package com.company.ticketing.model;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

 /** 
 * Tests 
 * @author txb272 - Daniel Hutchinson 
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

        Seat model = new Seat();

        model.setReserved(reserved);
        model.setNum(num);
        model.setHold(hold);
        model.setRow(row);

        assertEquals(false, model.getAvailable());
        assertEquals(reserved, model.getReserved());
        assertEquals(num, model.getNum());
        assertEquals(hold, model.getHold());
        assertEquals(row, model.getRow());
        assertEquals(model.hashCode(), model.hashCode());
        assertTrue(model.equals(model));
        assertEquals(model.toString(), model.toString());
    }
}
