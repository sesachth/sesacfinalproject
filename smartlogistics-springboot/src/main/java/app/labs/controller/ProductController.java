package app.labs.controller;

import app.labs.model.Product;
import app.labs.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
			@RequestParam(value = "fragile", defaultValue = "전체") String fragile) {

		
		Boolean fragileBool = null;
		if ("true".equalsIgnoreCase(fragile) || "yes".equalsIgnoreCase(fragile)) {
			fragileBool = true;
		} else if ("false".equalsIgnoreCase(fragile) || "no".equalsIgnoreCase(fragile)) {
			fragileBool = false;
		}

		
		List<Product> products;
		if ("전체".equals(filter) && fragileBool == null) {
			products = productService.getAllProducts();
		} else if (!"전체".equals(filter) && fragileBool == null) {
			products = productService.getProductByCategory(filter);
		} else if ("전체".equals(filter)) {
			products = productService.getProductByFragile(fragileBool);
		} else {
			products = productService.getProductByCategoryAndFragile(filter, fragileBool);
		}

		
		model.addAttribute("page", "product");
		model.addAttribute("products", products);
		model.addAttribute("selectedCategory", filter);
		model.addAttribute("selectedFragile", fragile);

		return "thymeleaf/html/admin/admin_product";
	}
	
	@GetMapping("/search1")
	@ResponseBody
	public List<Product> showProductPageSearch(Model model, @RequestParam(value = "filter", defaultValue = "전체") String filter,
			@RequestParam(value = "fragile", defaultValue = "전체") String fragile) {

		// fragile 조회
		Boolean fragileBool = null;
		if ("true".equalsIgnoreCase(fragile) || "yes".equalsIgnoreCase(fragile)) {
			fragileBool = true;
		} else if ("false".equalsIgnoreCase(fragile) || "no".equalsIgnoreCase(fragile)) {
			fragileBool = false;
		}

		// 필터링 로직
		List<Product> products;
		if ("전체".equals(filter) && fragileBool == null) {
			products = productService.getAllProducts();
		} else if (!"전체".equals(filter) && fragileBool == null) {
			products = productService.getProductByCategory(filter);
		} else if ("전체".equals(filter)) {
			products = productService.getProductByFragile(fragileBool);
		} else {
			products = productService.getProductByCategoryAndFragile(filter, fragileBool);
		}

		return products;
	}
	

    @GetMapping("/search")
	@ResponseBody
    public List<Product> searchProducts(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "filter", defaultValue = "전체") String filter,
            @RequestParam(value = "fragile", defaultValue = "전체") String fragile) {
        
        // 검색어 처리
        name = (name != null) ? name.trim() : "";
        
        // fragile 값 변환
        final Boolean fragileBool;
        if ("true".equalsIgnoreCase(fragile) || "yes".equalsIgnoreCase(fragile)) {
            fragileBool = Boolean.TRUE;
        } else if ("false".equalsIgnoreCase(fragile) || "no".equalsIgnoreCase(fragile)) {
            fragileBool = Boolean.FALSE;
        } else {
            fragileBool = null;
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
        
        // fragile 필터 적용
        if (fragileBool != null) {
            final Boolean targetFragile = fragileBool; // 람다에서 사용할 final 변수
            products = products.stream()
                .filter(p -> {
                    try {
                        Boolean isFragile = p.isFragile();
                        return isFragile != null && isFragile.equals(targetFragile);
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
	
	

}