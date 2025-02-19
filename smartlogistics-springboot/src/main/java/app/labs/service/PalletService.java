package app.labs.service;

import app.labs.dao.PalletRepository;
import app.labs.model.Pallet;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PalletService {

    private final PalletRepository palletRepository;

    public PalletService(PalletRepository palletRepository) {
        this.palletRepository = palletRepository;
    }

    /**
     * 필터링된 팔렛트 리스트를 페이징하여 조회
     */
    public List<Pallet> getFilteredPalletList(
            int offset, 
            int pageSize, 
            String palletId, 
            String destination, 
            String vehicleNumber) {
        return palletRepository.getFilteredPalletList(
            offset, 
            pageSize, 
            palletId, 
            destination, 
            vehicleNumber
        );
    }

    /**
     * 필터링된 전체 팔렛트 수 조회
     */
    public int getTotalFilteredRecords(
            String palletId, 
            String destination, 
            String vehicleNumber) {
        return palletRepository.countTotalFilteredPallet(
            palletId, 
            destination, 
            vehicleNumber
        );
    }
}