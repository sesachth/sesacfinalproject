package app.labs.dao;

import app.labs.model.Pallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PalletRepository {
    /**
     * 필터링된 팔렛트 리스트를 페이징하여 조회
     * @param offset 시작 위치
     * @param pageSize 페이지 크기
     * @param palletId 팔렛트 ID
     * @param destination 목적지
     * @param vehicleNumber 차량 번호
     * @return 필터링된 팔렛트 리스트
     */
    List<Pallet> getFilteredPalletList(
        @Param("offset") int offset,
        @Param("pageSize") int pageSize,
        @Param("palletId") String palletId,
        @Param("destination") String destination,
        @Param("vehicleNumber") String vehicleNumber
    );
   
    /**
     * 필터링된 전체 팔렛트 수 조회
     * @param palletId 팔렛트 ID
     * @param destination 목적지
     * @param vehicleNumber 차량 번호
     * @return 필터링된 전체 팔렛트 수
     */
    int countTotalFilteredPallet(
        @Param("palletId") String palletId,
        @Param("destination") String destination,
        @Param("vehicleNumber") String vehicleNumber
    );
}