package app.labs.service;

import app.labs.dao.ProgressDao;
import app.labs.model.ProgressDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProgressService {

    private final ProgressDao progressDao;

    public ProgressService(ProgressDao progressDao) {
        this.progressDao = progressDao;
    }

    public List<ProgressDTO> getFilteredProgressList(
        int offset, int pageSize, String date, String camp, String orderNum, String boxSpec, Integer boxState, Integer progressState) {
        return progressDao.getFilteredProgressList(offset, pageSize, date, camp, orderNum, boxSpec, boxState, progressState);
    }

    public int getTotalFilteredRecords(
        String date, String camp, String orderNum, String boxSpec, Integer boxState, Integer progressState) {
        return progressDao.getTotalFilteredRecords(date, camp, orderNum, boxSpec, boxState, progressState);
    }

    @Transactional
    public void updateOrdersProgress(List<Integer> orderIds, int progressState, Integer imageNumber) {
        if (orderIds == null || orderIds.isEmpty()) {
            System.out.println("⚠️ [DB 업데이트] 주문 ID 없음, 업데이트 수행하지 않음");
            return;
        }

        // ✅ imageNumber가 없을 경우 null로 설정
        System.out.println("📌 [DB 업데이트] 진행 상태 업데이트 실행 - 주문 ID: " + orderIds + ", progressState: " + progressState + ", imageNumber: " + imageNumber);
        
        progressDao.updateOrdersProgress(orderIds, progressState, imageNumber);
        System.out.println("✅ [DB 업데이트] 완료 - 업데이트된 주문 ID: " + orderIds);
    }
}
