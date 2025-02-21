package app.labs.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.labs.dao.DashboardRepository;

@Service
public class DashboardService {
	@Autowired
	private DashboardRepository dashboardRepository;
	
	public int getOrderCountByProgressState(int progressState) {
		return dashboardRepository.getOrderCountByProgressState(progressState);
	}
	
	public float getBoxDamageRateByOrderTime(LocalDateTime orderTime) {
		return dashboardRepository.getBoxDamageRateByOrderTime(orderTime);
	}
	
	public int getOrderCountByOrderTime(LocalDateTime orderTime) {
		return dashboardRepository.getOrderCountByOrderTime(orderTime);
	}
	
	public int getOrderCountByDestination(String destination) {
		return dashboardRepository.getOrderCountByDestination(destination);
	}
	
	public int getOrderCountBySpec(int spec) {
		return dashboardRepository.getOrderCountBySpec(spec);
	}
}
