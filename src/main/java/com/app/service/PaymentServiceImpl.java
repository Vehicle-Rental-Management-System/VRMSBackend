package com.app.service;

import java.time.LocalDate;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.dao.BankDetailsRepository;
import com.app.dao.BookingRepository;
import com.app.dao.PaymentRepository;
import com.app.dto.AddPaymentDto; 
import com.app.dto.ApiResponse;
import com.app.entities.BankDetailsEntity;
import com.app.entities.BookingDetailsEntity;
import com.app.entities.PaymentDetailsEntity;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {
 
	@Autowired
	private PaymentRepository paymentRepository; 
	
	@Autowired
	private BookingRepository bookingRepository; 
	
	@Autowired
	private BankDetailsRepository bankDetailsRepo;
	
	@Autowired
	private ModelMapper mapper;
	
	@Override 
	public ApiResponse addPayment(AddPaymentDto paymentDto) {
		
		BookingDetailsEntity bookingDetails = bookingRepository.findById(paymentDto.getBookingId()).orElseThrow(() -> new RuntimeException("Invalid booking Id"));
		
		BankDetailsEntity bankDetail = bankDetailsRepo
				.findByCardNoAndCvv(paymentDto.getCardNo(), paymentDto.getCvv());
		
		if(bankDetail != null && bankDetail.getExpiryDate().equals(paymentDto.getExpiryDate())) {
			bookingDetails.setStatus("Successfull");
			
			PaymentDetailsEntity paymentDetails = new PaymentDetailsEntity("Succesfull", paymentDto.getPaymentAmount(), LocalDate.now(), bookingDetails);		
			PaymentDetailsEntity paymentDetailsEntity = paymentRepository.save(paymentDetails);
			return new ApiResponse("payment details added succesfully");
		}
		else {
			bookingDetails.setStatus("UnSuccessfull");
			bookingDetails.getVehicle().setStatus("Available");
			return new ApiResponse("payment failed..");
			
		}
		
		
	}

}
