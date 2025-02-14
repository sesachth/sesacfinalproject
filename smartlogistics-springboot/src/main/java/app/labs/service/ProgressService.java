package app.labs.service;

import app.labs.dao.ProgressDao;
import app.labs.model.ProgressDTO;
import org.springframework.stereotype.Service;
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
}
