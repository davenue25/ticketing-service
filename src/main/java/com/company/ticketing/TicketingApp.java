package com.company.ticketing;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.company.ticketing.service.TicketService;

@SpringBootApplication
public class TicketingApp {

	private static long seatsHoldTimeMillis;
	
	private static TicketService service;
	
	@Autowired
	public static void setTicketService(TicketService s) {
		service = s;
	}
	
	// In real life, get this from config server or database
	@Value("${seats.holdTimeMillis:10}")
	public void setHoldSeatsTimeMillis(long millis) {
		this.seatsHoldTimeMillis = millis;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(TicketingApp.class, args);
		
		
		System.out.println("TicketServiceImple.initializeVenueSize() seatsHoldTimeMillis = " + seatsHoldTimeMillis);
		List<Integer> rows = new ArrayList<>();
		rows.add(1);
		rows.add(2);
		rows.add(3);
		service.initializeVenueSize(rows);
		service.findAndHoldSeats(4, "daniel");
	}
}

