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

    // GET /admin/product - 제품 목록 페이지 반환
    @GetMapping
    public String showProductPage(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "thymeleaf/html/product"; // thymeleaf/product.html 렌더링
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
    public String deleteProduct(@PathVariable int id) {
        productService.deleteProductById(id);
        return "redirect:/admin/product";
    }
}
