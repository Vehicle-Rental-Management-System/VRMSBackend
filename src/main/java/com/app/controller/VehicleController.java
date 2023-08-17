package com.app.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.dto.AddVehicalDto;
import com.app.dto.ApiResponse;
import com.app.dto.UpdateVehicleDto;
import com.app.dto.VehicleResponseDto;
import com.app.entities.Vehicle;
import com.app.service.VehicleService;


@RestController
@RequestMapping("/vehicle")
public class VehicleController {

	@Autowired
	private VehicleService vehicleService;
	
	
	@PostMapping
	public ApiResponse addVehicleToExistingLocation(@RequestBody @Valid AddVehicalDto vehicleDto) {
		return vehicleService.addVehicle(vehicleDto);
	}
	
	
	@DeleteMapping("/{id}")
	public ApiResponse removeVehicle(@PathVariable Long id) {	
		return vehicleService.deleteVehicle(id);
	}
	
	@GetMapping
	public List<VehicleResponseDto> getAllVehicles(){
		
		return vehicleService.getAllVehicles();
	}
	
	@PutMapping
	public ApiResponse updateVehicle(@RequestBody @Valid UpdateVehicleDto vehicleDto) {
		
		return vehicleService.updateVehicle(vehicleDto);
	}
	
}
