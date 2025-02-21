package app.labs.controller;

import app.labs.model.Product;
import app.labs.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/product")
public class ProductController {

	private final ProductService productService;

	@Autowired
	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	// GET /admin/product 
	@GetMapping
	public String showProductPage(Model model, @RequestParam(value = "filter", defaultValue = "전체") String filter,
			@RequestParam(value = "isFragile", defaultValue = "전체") String isFragile) {

		
		Boolean isFragileBool = null;
		if ("true".equalsIgnoreCase(isFragile) || "yes".equalsIgnoreCase(isFragile)) {
			isFragileBool = true;
		} else if ("false".equalsIgnoreCase(isFragile) || "no".equalsIgnoreCase(isFragile)) {
			isFragileBool = false;
		}

		
		List<Product> products;
		if ("전체".equals(filter) && isFragileBool == null) {
			products = productService.getAllProducts();
		} else if (!"전체".equals(filter) && isFragileBool == null) {
			products = productService.getProductByCategory(filter);
		} else if ("전체".equals(filter)) {
			products = productService.getProductByFragile(isFragileBool);
		} else {
			products = productService.getProductByCategoryAndFragile(filter, isFragileBool);
		}

		
		model.addAttribute("page", "product");
		model.addAttribute("products", products);
		model.addAttribute("selectedCategory", filter);
		model.addAttribute("selectedisFragile", isFragile);

		return "thymeleaf/html/admin/admin_product";
	}
	
	@GetMapping("/search1")
	@ResponseBody
	public List<Product> showProductPageSearch(Model model, @RequestParam(value = "filter", defaultValue = "전체") String filter,
			@RequestParam(value = "isFragile", defaultValue = "전체") String isFragile) {

		// isFragile 조회
		Boolean isFragileBool = null;
		if ("true".equalsIgnoreCase(isFragile) || "yes".equalsIgnoreCase(isFragile)) {
			isFragileBool = true;
		} else if ("false".equalsIgnoreCase(isFragile) || "no".equalsIgnoreCase(isFragile)) {
			isFragileBool = false;
		}

		// 필터링 로직
		List<Product> products;
		if ("전체".equals(filter) && isFragileBool == null) {
			products = productService.getAllProducts();
		} else if (!"전체".equals(filter) && isFragileBool == null) {
			products = productService.getProductByCategory(filter);
		} else if ("전체".equals(filter)) {
			products = productService.getProductByFragile(isFragileBool);
		} else {
			products = productService.getProductByCategoryAndFragile(filter, isFragileBool);
		}

		return products;
	}
	

    @GetMapping("/search")
	@ResponseBody
    public List<Product> searchProducts(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "filter", defaultValue = "전체") String filter,
            @RequestParam(value = "isFragile", defaultValue = "전체") String isFragile) {
        
        // 검색어 처리
        name = (name != null) ? name.trim() : "";
        
        // isFragile 값 변환
        final Boolean isFragileBool;
        if ("true".equalsIgnoreCase(isFragile) || "yes".equalsIgnoreCase(isFragile)) {
            isFragileBool = Boolean.TRUE;
        } else if ("false".equalsIgnoreCase(isFragile) || "no".equalsIgnoreCase(isFragile)) {
            isFragileBool = Boolean.FALSE;
        } else {
            isFragileBool = null;
        }

        // 검색 로직
        List<Product> products;
        
        // 기본 검색 수행
        if (!name.isEmpty()) {
            products = productService.getProductsByName(name);
        } else {
            products = productService.getAllProducts();
        }
        
        // 필터 적용
        if (!"전체".equals(filter)) {
            products = products.stream()
                .filter(p -> filter.equals(p.getCategory()))
                .collect(Collectors.toList());
        }
        
        // isFragile 필터 적용
        if (isFragileBool != null) {
            final Boolean targetisFragile = isFragileBool; // 람다에서 사용할 final 변수
            products = products.stream()
                .filter(p -> {
                    try {
                        Boolean isisFragile = p.isFragile();
                        return isisFragile != null && isisFragile.equals(targetisFragile);
                    } catch (Exception e) {
                        return false; // 에러 발생 시 해당 항목 제외
                    }
                })
                .collect(Collectors.toList());
        }

        return products;
    }

	// POST /admin/product/add - 뭂품 추가
	@PostMapping("/add")
	@ResponseBody
	public String addProduct(@ModelAttribute Product product) {
		productService.addProduct(product);
		return "redirect:/admin/product";
	}

	// POST /admin/product/update - 물품수정
	@PostMapping("/update")
	@ResponseBody
	public String updateProduct(@ModelAttribute Product product) {
		productService.updateProduct(product);
		return "redirect:/admin/product";
	}

	// GET /admin/product/delete/{id} - 물품 삭제
	@GetMapping("/delete/{id}")
	public String deleteProduct(@PathVariable("id") int id) {
		productService.deleteProductById(id);
		return "redirect:/admin/product";
	}
	
	@GetMapping("/excel")
	public ResponseEntity<InputStreamResource> exportToExcel() throws IOException {
		String filename = "products.xlsx";
		ByteArrayInputStream in = productService.exportProductsToExcel();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=" + filename);

		return ResponseEntity
				.ok()
				.headers(headers)
				.contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
				.body(new InputStreamResource(in));
	}

}