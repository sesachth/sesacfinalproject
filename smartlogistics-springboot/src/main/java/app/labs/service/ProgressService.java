package app.labs.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.labs.dao.ProgressDao;
import app.labs.model.ProgressDTO;

@Service
public class ProgressService {

    private final ProgressDao progressDao;
    private final SimpMessagingTemplate messagingTemplate;

    public ProgressService(ProgressDao progressDao, SimpMessagingTemplate messagingTemplate) {
        this.progressDao = progressDao;
        this.messagingTemplate = messagingTemplate;
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
        
        sendImageUpdateToClients(orderIds, imageNumber);
    }
    
    public void sendImageUpdateToClients(List<Integer> orderIds, Integer imageNumber) {
        if (imageNumber == null) {
            System.out.println("⚠️ [이미지 전송] 이미지 번호 없음");
            return;
        }

        String imageUrl = "/images/boximages/" + String.format("%03d", imageNumber) + ".jpg";
        messagingTemplate.convertAndSend("/topic/updateImage", Map.of(
                "orderIds", orderIds,
                "imageUrl", imageUrl
        ));
        System.out.println("📡 [WebSocket 전송] 주문 ID: " + orderIds + ", URL: " + imageUrl);
    }
    
    public void updateBoxState(Long orderId, int boxState) {
    	progressDao.updateBoxState(orderId, boxState);  // MyBatis 매퍼 호출
    	
    	// WebSocket으로 상태 변경 알림
        Map<String, Object> message = new HashMap<>();
        message.put("orderId", orderId);
        message.put("boxState", boxState);
        
        messagingTemplate.convertAndSend("/topic/updateBoxState", message);
        System.out.println("📦 박스 상태 업데이트 완료 - 주문 ID: " + orderId + ", 상태: " + boxState);
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
