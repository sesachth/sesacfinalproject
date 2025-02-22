package app.labs.dao;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DashboardRepository {
	int getOrderCountByProgressState(int progressState);
	float getBoxDamageRateByOrderTime(LocalDateTime orderTime);
	int getOrderCountByOrderTime(LocalDateTime orderTime);
	int getOrderCountByDestination(String destination);
	int getOrderCountBySpec(int spec);
}
