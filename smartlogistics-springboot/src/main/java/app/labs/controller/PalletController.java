package app.labs.controller;

import app.labs.service.PalletService;
import app.labs.model.Pallet;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            // @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "destination", required = false) String destination,
            @RequestParam(value = "vehicleNumber", required = false) String vehicleNumber,
            Model model) {

        int pageSize = 20;
        int offset = (page - 1) * pageSize;

        List<Pallet> palletList = palletService.getFilteredPalletList(offset, pageSize, destination, vehicleNumber);
        int totalRecords = palletService.getTotalFilteredRecords( destination, vehicleNumber);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        model.addAttribute("palletList", palletList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("page", "pallet");

        return "thymeleaf/html/admin/admin_pallet";
    }

    @GetMapping("/data")
    @ResponseBody
    public Map<String, Object> getPalletData(
            @RequestParam(value = "page", defaultValue = "1") int page,
            // @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "destination", required = false) String destination,
            @RequestParam(value = "vehicleNumber", required = false) String vehicleNumber) {

        int pageSize = 20;
        int offset = (page - 1) * pageSize;

        List<Pallet> palletList = palletService.getFilteredPalletList(offset, pageSize, destination, vehicleNumber);
        int totalRecords = palletService.getTotalFilteredRecords( destination, vehicleNumber);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        Map<String, Object> response = new HashMap<>();
        response.put("palletList", palletList);
        response.put("totalPages", totalPages);
        return response;
    }
}
