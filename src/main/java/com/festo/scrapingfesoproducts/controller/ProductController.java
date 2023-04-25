package com.festo.scrapingfesoproducts.controller;


import com.festo.scrapingfesoproducts.entity.Product;
import com.festo.scrapingfesoproducts.service.FestoService;
/*
import com.festo.scrapingfesoproducts.service.SiemensService;
*/
/*
import com.festo.scrapingfesoproducts.service.SiemensService;
*/
import com.festo.scrapingfesoproducts.service.SiemensService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ProductController {

    @Autowired
    private FestoService festoService;

    @Autowired
    private SiemensService siemensService;



Product product = new Product();
    @GetMapping("/festo/product-information/{productId}")
    public Map<String, Object> getProductTitle(@PathVariable String productId) throws IOException, JSONException {
        festoService.scrapeAndStoreProductFeatures(productId);

        Map<String, Object> response = new HashMap<>();

        response.put("Product Title", festoService.productTitle(productId));
        response.put("Product Information", festoService.productInformation(productId));

        return response;

    }


    @GetMapping("/siemens/product-information/{productId}")
    public Map<String, Object> getSiemensProducts(@PathVariable String productId) throws IOException, JSONException, InterruptedException {

          siemensService.scrape(productId);
        Map<String, Object> response = new HashMap<>();
        response.put("Product Title", siemensService.productTitle(productId));
        response.put("Product Information", siemensService.productInformation(productId));

          return response;

    }


}
