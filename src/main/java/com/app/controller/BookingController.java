package com.app.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.dto.ApiResponse;
import com.app.dto.BookingDto;
import com.app.dto.BookingResponseDto;
import com.app.dto.CancelBookingDto;
import com.app.service.BookingService;

@RestController
@RequestMapping("/booking")
public class BookingController {
	
	@Autowired
	private BookingService bookingService;
	

	@PostMapping("/addbooking/{userId}")
	public ApiResponse addBookingDetails(@RequestBody BookingDto booking, @PathVariable Long userId) {
		
		
		return bookingService.addBookingDetails(booking,userId);
		
	}
	
	@GetMapping("/{userId}")
	public List<BookingDto> getMyBookings(@PathVariable Long userId) {
		
		return bookingService.getMyBookings(userId);
	}
	
	@GetMapping("/allbookings")
	public List<BookingResponseDto> getAllBookings(){
		
		return bookingService.getAllBookings();
	}
	
	@DeleteMapping("/cancel_booking")
	public ApiResponse cancelBooking(@RequestBody @Valid CancelBookingDto cancelBookingDto) {
		return bookingService.cancelBooking(cancelBookingDto);
	}
	
}
