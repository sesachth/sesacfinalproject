package app.labs.service;

import app.labs.dao.PalletRepository;
import app.labs.model.Pallet;
import app.labs.model.Product;
import app.labs.model.ProgressDTO;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    
    
    public ByteArrayInputStream exportPalletListToExcel(String palletId, String destination, String vehicleNumber)
            throws IOException {
        // 필터된 전체 데이터 (페이징 없이 전체 조회)
        List<Pallet> list = palletRepository.getFilteredPalletList(
                0, // offset
                Integer.MAX_VALUE, // 매우 큰 pageSize
                palletId,
                destination,
                vehicleNumber
        );

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Pallets");

            // ── Header 스타일 설정 ──
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // ── 헤더 행 생성 ──
            Row headerRow = sheet.createRow(0);
            // 각 컬럼 생성: 순서대로 Pallet ID, Load, Width, Depth, Height, Destination, Vehicle Number
            String[] headers = {"Pallet ID", "Load", "Width", "Depth", "Height", "Destination", "Vehicle Number"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                // 옵션: 각 컬럼 간격 자동조정 (나중에 workbook.write(out) 후 적용 가능)
                sheet.setColumnWidth(i, 4000);
            }

            // ── 데이터 행 채우기 ──
            int rowIdx = 1;
            for (Pallet pallet : list) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(pallet.getPalletId());
                row.createCell(1).setCellValue(pallet.getLoad());
                row.createCell(2).setCellValue(pallet.getWidth());
                row.createCell(3).setCellValue(pallet.getDepth());
                row.createCell(4).setCellValue(pallet.getHeight());
                row.createCell(5).setCellValue(pallet.getDestination());
                row.createCell(6).setCellValue(pallet.getVehicleNumber());
            }

            // 엑셀 파일 작성 및 ByteArrayInputStream으로 반환
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    } 
    
}