package app.labs.controller;

import app.labs.model.Product;
import app.labs.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/product")
public class ProductController {

	private final ProductService productService;

	@Autowired
	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	// GET /admin/product - 전체 또는 카테고리 필터링 제품 목록 반환
	@GetMapping
	public String showProductPage(Model model, @RequestParam(value = "filter", defaultValue = "전체") String filter,
			@RequestParam(value = "fragile", defaultValue = "전체") String fragile) {
// fragile 값 변환 및 검증
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

// 모델에 데이터 추가
		model.addAttribute("page", "product");
		model.addAttribute("products", products);
		model.addAttribute("selectedCategory", filter);
		model.addAttribute("selectedFragile", fragile);

		return "thymeleaf/html/admin/admin_product";
	}



	// POST /admin/product/add - 제품 추가 처리
	@PostMapping("/add")
	public String addProduct(@ModelAttribute Product product) {
		productService.addProduct(product);
		return "redirect:/admin/product";
	}

	// POST /admin/product/update - 제품 수정 처리
	@PostMapping("/update")
	public String updateProduct(@ModelAttribute Product product) {
		productService.updateProduct(product);
		return "redirect:/admin/product";
	}

	// GET /admin/product/delete/{id} - 제품 삭제 처리
	@GetMapping("/delete/{id}")
	public String deleteProduct(@PathVariable("id") int id) {
		productService.deleteProductById(id);
		return "redirect:/admin/product";
	}
	


}