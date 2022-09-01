package com.parkit.parkingsystem.service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		double duration = calculateDuration(ticket);
		double calculation;

		switch (ticket.getParkingSpot().getParkingType()) {
		case CAR: {
			calculation = duration * Fare.CAR_RATE_PER_HOUR;
			break;
		}
		case BIKE: {
			calculation = duration * Fare.BIKE_RATE_PER_HOUR;
			break;
		}
		default:
			throw new IllegalArgumentException("Unkown Parking Type");
		}

		if (duration <= 0.5) {
			ticket.setPrice(0);
		} else {
			if (ticket.getDiscount() == true) {
				double reduction = calculation * Fare.REDUCTION_POURCENTGAGE /100;
				ticket.setPrice((Math.round((calculation - reduction) * 100.0) / 100.0));
			} else {
				ticket.setPrice(Math.round(calculation * 100.0) / 100.0);
			}
		}
	}

	public double calculateDuration(Ticket ticket) {
		Instant inHour = ticket.getInTime().toInstant();
		Instant outHour = ticket.getOutTime().toInstant();

		LocalDateTime localInHour = LocalDateTime.ofInstant(inHour, ZoneId.systemDefault());
		LocalDateTime localOutHour = LocalDateTime.ofInstant(outHour, ZoneId.systemDefault());

		Duration duration = Duration.between(localInHour, localOutHour);

		return duration.getSeconds() / 3600;
	}
}