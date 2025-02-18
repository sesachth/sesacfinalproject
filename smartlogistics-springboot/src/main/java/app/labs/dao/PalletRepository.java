package app.labs.dao;

import app.labs.model.Pallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PalletRepository {
	   List<Pallet> getFilteredPalletList(
		        @Param("offset") int offset,
		        @Param("pageSize") int pageSize,
		        @Param("destination") String destination,
		        @Param("vehicleNumber") String vehicleNumber
		    );
	   
	    int countTotalFilteredPallet(
	            @Param("destination") String destination,
	            @Param("vehicleNumber") String vehicleNumber
	        );
}