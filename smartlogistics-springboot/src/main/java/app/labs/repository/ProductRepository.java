package app.labs.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import app.labs.model.Product;

import java.util.List;

@Mapper
public interface ProductRepository {
    @Select("SELECT * FROM product")
    List<Product> getAllProducts();
}
