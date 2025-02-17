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

    public List<Pallet> getFilteredPalletList(int offset, int pageSize, String destination, String vehicleNumber) {
        return palletRepository.getFilteredPalletList(offset, pageSize, destination, vehicleNumber);
    }

    public int getTotalFilteredRecords( String destination, String vehicleNumber) {
        return palletRepository.countTotalFilteredPallet( destination, vehicleNumber);
    }

	
}
