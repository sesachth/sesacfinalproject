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

    public List<ProgressDTO> getFilteredProgressList(int offset, int pageSize, String date, String camp, String orderNum) {
        return progressDao.getFilteredProgressList(offset, pageSize, date, camp, orderNum);
    }

    public int getTotalFilteredRecords(String date, String camp, String orderNum) {
        return progressDao.countTotalFilteredProgress(date, camp, orderNum);
    }

    // ✅ 진행 상태 업데이트 추가
    @Transactional
    public void updateProgressState(Long orderId, int progressState) {
        if (orderId == null || progressState < 0) {
            throw new IllegalArgumentException("유효하지 않은 주문 ID 또는 진행 상태입니다.");
        }
        progressDao.updateProgressState(orderId, progressState);
    }

}
