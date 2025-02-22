package app.labs.service;

import java.util.List;
import java.util.Map;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import app.labs.dao.ProductlistRepository;
import app.labs.model.Product;

@Service
public class ProductService {

    private final ProductlistRepository productlistRepository;
    private final String FASTAPI_URL = "http://localhost:8000/api/v1/box_matching"; // FastAPI URL

    @Autowired
    public ProductService(ProductlistRepository productlistRepository) {
        this.productlistRepository = productlistRepository;
    }

    public List<Product> getAllProducts() {
        return productlistRepository.findAll();
    }

    // 移댄뀒怨좊━蹂� �쒗뭹 議고쉶 
    public List<Product> getProductByCategory(String category) {
        return productlistRepository.findByCategory(category);
    }
    
    // 痍④툒二쇱쓽蹂� �쒗뭹 議고쉶
    public List<Product> getProductByFragile(boolean isFragile) {
        return productlistRepository.findByFragile(isFragile);
    }

    // 移댄뀒怨좊━�� 痍④툒 二쇱쓽(Fragile) 媛믪뿉 �곕Ⅸ �쒗뭹 議고쉶
    public List<Product> getProductByCategoryAndFragile(String category, boolean isFragile) {
        return productlistRepository.findByCategoryAndFragile(category, isFragile);
    }
    
    public void addProduct(Product product) {
        // FastAPI로 박스 매칭 결과 받기
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = null;
        
        try {
        	ObjectMapper mapper = new ObjectMapper();

            // Product 객체를 Map으로 변환
            Map<String, Object> productMap = mapper.convertValue(product, Map.class);
           
            
            // isFragile 필드 이름을 is_fragile로 변경
            if (productMap.containsKey("fragile")) {
                productMap.put("is_fragile", productMap.remove("fragile"));
                // Map을 JSON 형식으로 FastAPI에 전달
                response = restTemplate.postForObject(FASTAPI_URL, productMap, Map.class);
            } else {
                // Product를 JSON 형식으로 FastAPI에 전달
                response = restTemplate.postForObject(FASTAPI_URL, product, Map.class);   
            }
           
            if (response != null && response.get("spec") != null) {
                product.setSpec(Long.parseLong(response.get("spec").toString()));
            } else {
                product.setSpec(0L); // 박스 매칭 실패 시 default 값
            }

            productlistRepository.insert(product);
        } catch (HttpServerErrorException | HttpClientErrorException e) {
            // FastAPI 서버에서 에러 발생 시 처리
            System.err.println("Error during FastAPI communication: " + e.getMessage());
            product.setSpec(0L); // 기본값 처리
            productlistRepository.insert(product); // 계속해서 DB에 저장
        } catch (Exception e) {
            // 기타 예외 처리
            e.printStackTrace();
            product.setSpec(0L); // 기본값 처리
            productlistRepository.insert(product); // 계속해서 DB에 저장
        }
        // DB에 저장
        // productlistRepository.insert(product);
    }

    public boolean updateProduct(Product product) {
        return productlistRepository.update(product) > 0;
    }

    public boolean deleteProductById(int productId) {
        return productlistRepository.delete(productId) > 0;
    }
    
    // �대쫫�쇰줈 �쒗뭹 寃���
    public List<Product> getProductsByName(String name) {
        return productlistRepository.findByNameContaining(name);
    }
    
    public ByteArrayInputStream exportProductsToExcel() throws IOException {
        List<Product> products = getAllProducts();
        
        try (Workbook workbook = new XSSFWorkbook(); 
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("제품 목록");
            
            // 헤더 스타일
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 데이터 스타일
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.CENTER);

            // 헤더 생성
            Row headerRow = sheet.createRow(0);
            String[] columns = {"제품 ID", "제품명", "너비(cm)", "깊이(cm)", "높이(cm)", "무게(kg)", "취급주의", "카테고리", "박스규격"};
            
            for (int col = 0; col < columns.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columns[col]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(col, 3500);  // 컬럼 너비 설정
            }

            // 데이터 입력
            int rowIdx = 1;
            for (Product product : products) {
                Row row = sheet.createRow(rowIdx++);
                
                row.createCell(0).setCellValue(product.getProductId());
                row.createCell(1).setCellValue(product.getName());
                row.createCell(2).setCellValue(product.getWidth());
                row.createCell(3).setCellValue(product.getDepth());
                row.createCell(4).setCellValue(product.getHeight());
                row.createCell(5).setCellValue(product.getWeight());
                row.createCell(6).setCellValue(product.isFragile() ? "Yes" : "No");
                row.createCell(7).setCellValue(product.getCategory());
                row.createCell(8).setCellValue(product.getSpec() != null ? product.getSpec() : 0);

                // 모든 셀에 데이터 스타일 적용
                for (int col = 0; col < columns.length; col++) {
                    Cell cell = row.getCell(col);
                    if (cell != null) {
                        cell.setCellStyle(dataStyle);
                    }
                }
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}