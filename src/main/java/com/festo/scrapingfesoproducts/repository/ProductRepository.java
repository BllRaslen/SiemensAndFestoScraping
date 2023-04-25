package com.festo.scrapingfesoproducts.repository;

import com.festo.scrapingfesoproducts.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    @Query(value = "SELECT  DISTINCT  \"key\" , \"value\"\n" +
            "FROM     \"public\".\"product\" \n" +
            "INNER JOIN \"public\".\"technical_data\"  ON \"public\".\"product\".\"product_id\" = \"public\".\"technical_data\".\"product_id_fk\"  AND product_id = ?",
            nativeQuery = true)
    List<Map<String, String>> allInfo(String productId);

    Product findByProductId(String productId);

    @Query(value = "SELECT DISTINCT  \"public\".\"product\".*\n" +
            "FROM     \"public\".\"product\" \n" +
            "INNER JOIN \"public\".\"technical_data\"  ON \"public\".\"product\".\"product_id\" = \"public\".\"technical_data\".\"product_id_fk\"    AND product_id = ?",
            nativeQuery = true)
    Map<String, String> productTitle(String productId);


}
