package com.festo.scrapingfesoproducts.repository;

import com.festo.scrapingfesoproducts.entity.Product;
import com.festo.scrapingfesoproducts.entity.TechnicalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;



@Repository

public interface TechnicalDataRepository extends JpaRepository<TechnicalData, Long> {

    @Query(value = "SELECT * FROM product",
            nativeQuery = true)
    List<Map<String, String>> findFeaturesByProductId(String productId);




   /* @Query(value = "SELECT   \"public\".\"product\".\"product_id\",\n" +
            "         \"public\".\"product\".\"product_name\"\n" +
            "FROM     \"public\".\"product\" \n" +
            "INNER JOIN \"public\".\"technical_data\"  ON \"public\".\"product\".\"product_id\" = \"public\".\"technical_data\".\"product_id_fk\" " ,
            nativeQuery = true)
    List<Product> productTitle(Long productId);*/
}