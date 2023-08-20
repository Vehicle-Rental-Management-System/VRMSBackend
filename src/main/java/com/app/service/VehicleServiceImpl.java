package com.app.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.dao.BookingRepository;
import com.app.dao.ServiceLocationRepository;
import com.app.dao.VehicleBrandRepository;
import com.app.dao.VehicleRepository;
import com.app.dao.VehicleTypeRepository;
import com.app.dto.AddVehicalDto;
import com.app.dto.ApiResponse;
import com.app.dto.ServiceLocationDto;
import com.app.dto.ServiceLocationResponseDto;
import com.app.dto.UpdateVehicleDto;
import com.app.dto.VehicleResponseDto;
import com.app.entities.BookingDetailsEntity;
import com.app.entities.ServiceLocationEntity;
import com.app.entities.Vehicle;
import com.app.entities.VehicleBrand;
import com.app.entities.VehicleType;


@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

	@Autowired
	private VehicleRepository vehicleRepo;
	
	@Autowired
	private ServiceLocationRepository serviceLocationRepo;
	
	@Autowired
	private VehicleBrandRepository vehicleBrandRepo;
	
	
	@Autowired
	private VehicleTypeRepository vehicleTypeRepo;
	
	@Autowired
	private BookingRepository bookingRepo;
	
	@Autowired
	private ModelMapper mapper;
	
	@Override
	public ApiResponse addVehicle(AddVehicalDto vehicleDto) {

		
		ServiceLocationEntity location = serviceLocationRepo.findById(vehicleDto.getServiceLocationId()).orElseThrow(()->new RuntimeException("location not found"));
		VehicleBrand vehicleBrand = vehicleBrandRepo.findById(vehicleDto.getBrandId()).orElseThrow(()->new RuntimeException("Brand not found"));
		VehicleType vehicleType = vehicleTypeRepo.findById(vehicleDto.getTypeId()).orElseThrow(()->new RuntimeException("Type not found"));
		
				
		Vehicle vehicle = mapper.map(vehicleDto, Vehicle.class);
		vehicle.setBrand(vehicleBrand);
		vehicle.setType(vehicleType);
		vehicle.setStatus("Available");
		location.addVehicle(vehicle);
		
		
		
		return new ApiResponse("Vehicle added");
	}
	
	@Override
	public ApiResponse deleteVehicle(Long id) {
		
		Vehicle vehicle = vehicleRepo.findById(id).orElseThrow(()-> new RuntimeException("vehicle not found"));
		ServiceLocationEntity location = serviceLocationRepo.
				findById(vehicle.getServiceLocation().getId())
				.orElseThrow(()-> new RuntimeException("location not found"));
		//vehicleRepo.delete(vehicle);
		
		List<BookingDetailsEntity> booking = bookingRepo.findByVehicle(vehicle)
										.orElseThrow(()-> new RuntimeException("booking not found"));
	
		
		booking.forEach(v-> v.setVehicle(null)); 
		
		
		location.removeVehicle(vehicle);
		return new ApiResponse("vehicle deleted");
		
	}
	
	@Override
	public List<VehicleResponseDto> getAllVehicles() {
		List<Vehicle> vehicleList = vehicleRepo.findAll();
		
		
		List<VehicleResponseDto> vehicleListResponse = vehicleList.stream() //Stream<Emp>
		.map(vehicle -> mapper.map(vehicle, VehicleResponseDto.class)) //Stream<DTO>
		.collect(Collectors.toList());
		
		for(int i=0;i<vehicleList.size();i++) {
			vehicleListResponse.get(i).setLocation(mapper.map(vehicleList.get(i).getServiceLocation(), ServiceLocationResponseDto.class));
		}
		
		return vehicleListResponse;
	
	}
	
	@Override
	public List<VehicleResponseDto> getAvailableVehicles() {
		
		return getAllVehicles().stream()
				.filter(vehicle -> vehicle.getStatus().equalsIgnoreCase("Available"))
				.collect(Collectors.toList());
		
	}
	
	@Override
	public List<VehicleResponseDto> getReservedVehicles() {
		return getAllVehicles().stream()
		.filter(vehicle -> vehicle.getStatus().equalsIgnoreCase("NotAvailable"))
		.collect(Collectors.toList());
	}
	
	@Override
	public Set<VehicleResponseDto> getAllVehiclesByServiceLocation(Long id) {
		
		ServiceLocationEntity location = serviceLocationRepo.findById(id)
										.orElseThrow(()-> new RuntimeException("Service Location not found"));
		
		Set<Vehicle> vehicleList = location.getVehicleSet();
		
		
		return  vehicleList.stream() 
				.filter(vehicle-> vehicle.getStatus().equalsIgnoreCase("Available"))
				.map(vehicle -> mapper.map(vehicle, VehicleResponseDto.class)) //Stream<DTO>
		.collect(Collectors.toSet());
	
	}
	
	@Override
	public VehicleResponseDto getVehicleById(Long vehicleId) {
		Vehicle vehicle = vehicleRepo.findById(vehicleId)
										.orElseThrow(()-> new RuntimeException("Vehicle not found"));
		
		VehicleResponseDto vehicleResponse = mapper.map(vehicle, VehicleResponseDto.class);
		
		return vehicleResponse;
	}
	
	@Override
	public ApiResponse updateVehicle(UpdateVehicleDto vehicleDto) {
	
		Vehicle vehicle = vehicleRepo.findById(vehicleDto.getId())
				.orElseThrow(()-> new RuntimeException("Vehicle not found"));
		ServiceLocationEntity newLocation = serviceLocationRepo.findById(vehicleDto.getServiceLocationId())
												.orElseThrow(()-> new RuntimeException("location not found"));
		ServiceLocationEntity oldLocation = serviceLocationRepo.findById(vehicle.getServiceLocation().getId())
				.orElseThrow(()-> new RuntimeException("location not found"));
		
		vehicle.setFuelType(vehicleDto.getFuelType());
		
		if(oldLocation.getId()== newLocation.getId()) {
				vehicleRepo.save(vehicle);
		}
		else {
			
			oldLocation.removeVehicle(vehicle);
			newLocation.addVehicle(vehicle);
		}
		
		return new ApiResponse("vehicle updated");
	}
	
	
}
