package app.labs.service;

import app.labs.dao.ProgressDao;
import app.labs.model.ProgressDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    
 // ✅ 엑셀 파일을 생성하여 다운로드하는 메서드
    public byte[] generateExcelFile(String date, String camp, String orderNum, String boxSpec, Integer boxState, Integer progressState) throws IOException {
        List<ProgressDTO> progressList = progressDao.getFilteredProgressList(0, Integer.MAX_VALUE, date, camp, orderNum, boxSpec, boxState, progressState);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Progress Data");

        // ✅ 엑셀 헤더 설정
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Order ID", "Product Name", "Category", "Box Spec", "Pallet ID", "Box State", "Progress State"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(getHeaderCellStyle(workbook));
        }

        // ✅ 데이터 삽입
        int rowNum = 1;
        for (ProgressDTO progress : progressList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(progress.getOrderId());
            row.createCell(1).setCellValue(progress.getProductName());
            row.createCell(2).setCellValue(progress.getProductCategory());
            row.createCell(3).setCellValue(String.valueOf(progress.getBoxSpec()));
            row.createCell(4).setCellValue(String.valueOf(progress.getPalletId()));
            row.createCell(5).setCellValue(progress.getBoxState() == 0 ? "미검사" : progress.getBoxState() == 1 ? "정상" : "파손");
            row.createCell(6).setCellValue(progress.getProgressState() == 0 ? "물품 대기" : progress.getProgressState() == 1 ? "포장 완료" : "적재 완료");
        }

        // ✅ 자동 크기 조정
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // ✅ 엑셀 데이터를 바이트 배열로 변환
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    // ✅ 엑셀 헤더 스타일 설정
    private CellStyle getHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}
