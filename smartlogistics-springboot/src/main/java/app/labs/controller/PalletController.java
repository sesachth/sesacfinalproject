package app.labs.controller;

import app.labs.service.PalletService;
import jakarta.servlet.http.HttpServletResponse;
import app.labs.model.Pallet;

import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/pallet")
public class PalletController {

    private final PalletService palletService;

    public PalletController(PalletService palletService) {
        this.palletService = palletService;
    }

    @GetMapping
    public String getPalletList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "palletId", required = false) String palletId,
            @RequestParam(value = "destination", required = false) String destination,
            @RequestParam(value = "vehicleNumber", required = false) String vehicleNumber,
            Model model) {

        try {
            int pageSize = 20;
            int offset = (page - 1) * pageSize;

            // 데이터 조회
            List<Pallet> palletList = palletService.getFilteredPalletList(
                offset, pageSize, palletId, destination, vehicleNumber
            );
            
            // 전체 레코드 수 조회
            int totalRecords = palletService.getTotalFilteredRecords(
                palletId, destination, vehicleNumber
            );
            
            // 전체 페이지 수 계산
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

            // 모델에 데이터 추가
            model.addAttribute("palletList", palletList);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("palletId", palletId);
            model.addAttribute("destination", destination);
            model.addAttribute("vehicleNumber", vehicleNumber);
            model.addAttribute("page", "pallet");

            return "thymeleaf/html/admin/admin_pallet";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "데이터 조회 중 오류가 발생했습니다.");
            return "thymeleaf/html/admin/admin_pallet";
        }
    }

    // 데이터 조회용 API 엔드포인트 (필요한 경우)
    @GetMapping("/api/list")
    @ResponseBody
    public List<Pallet> getPalletListApi(
            @RequestParam(value = "palletId", required = false) String palletId,
            @RequestParam(value = "destination", required = false) String destination,
            @RequestParam(value = "vehicleNumber", required = false) String vehicleNumber) {

        // offset과 pageSize를 제거하고 전체 데이터를 반환
        return palletService.getFilteredPalletList(
            0,  // offset
            Integer.MAX_VALUE,  // pageSize를 매우 큰 값으로 설정
            palletId, 
            destination, 
            vehicleNumber
        );
    }
    
    
 // ── 엑셀 다운로드 엔드포인트 ──
    @GetMapping("/download/excel")
    public void downloadExcel(@RequestParam(value = "palletId", required = false) String palletId,
                              @RequestParam(value = "destination", required = false) String destination,
                              @RequestParam(value = "vehicleNumber", required = false) String vehicleNumber,
                              HttpServletResponse response) {
        try {
            ByteArrayInputStream bis = palletService.exportPalletListToExcel(palletId, destination, vehicleNumber);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=pallets.xlsx");

            IOUtils.copy(bis, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
            // 필요 시 오류 처리 추가
        }
    }
}