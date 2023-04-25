package com.festo.scrapingfesoproducts.service;


import com.festo.scrapingfesoproducts.entity.Product;
import com.festo.scrapingfesoproducts.entity.TechnicalData;
import com.festo.scrapingfesoproducts.repository.ProductRepository;
import com.festo.scrapingfesoproducts.repository.TechnicalDataRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



@Service
public class FestoService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TechnicalDataRepository technicalDataRepository;

    public void scrapeAndStoreProductFeatures(String productId) throws JSONException, IOException {

        Product existingProduct = productRepository.findByProductId(productId);


        Product product = new Product();
        product.setProductProducer("festo");
        if (existingProduct == null) {

            //Technical Data
            Document doc = null;

                doc = Jsoup.connect("https://www.festo.com/us/en/a/" + productId).get();

            Element featureList = doc.selectFirst("div.jsx-technical-data-table.js-technical-data");

            //Json Data
            JSONObject jsonObject = new JSONObject(featureList.attr("data-features"));
            JSONArray features = jsonObject.getJSONArray("features");

            //Product Name
            Element productName = doc.getElementById("main-headline");

            //Product Code
            Elements productCodeElement = doc.getElementsByClass("product-summary-article__order-code");
            String productCode = productCodeElement.text().replace("Product Code: ", "");
            //set all Technical data Columns
            Long ii = 0L;

            product.setProductId(productId);
            product.setProductCode(productCode);
            product.setProductName(productName.text());

            List<TechnicalData> productFeatures = new ArrayList<>();
            for (int i = 0; i < features.length(); i++) {
                JSONObject feature = features.getJSONObject(i);
                //Find the key
                String key = feature.getString("name");

                JSONArray featureValues = feature.getJSONArray("featureValues");
                for (int j = 0; j < featureValues.length(); j++) {

                    //Get JSON Object for Values
                    JSONObject featureValue = featureValues.getJSONObject(j);

                    //Find the value
                    String value = featureValue.getString("value");

                    // Find the unit of the value
                    String unit = feature.getString("unit");

                    // new data object
                    TechnicalData productFeature = new TechnicalData();

                    //set all Technical data Columns

                    //productFeature.setProductIdFk(product);
                    productFeature.setTechnicalDataId( ii); ii++;
                    productFeature.setName(key);
                    productFeature.setValue(value.trim() + " " + unit);
                    productFeature.setProductIdFk(product.getProductId());
                    productFeature.setProduct(product);
                    productFeatures.add(productFeature);
                }
            }
            productRepository.save(product);
            technicalDataRepository.saveAll(productFeatures);
            //store information in database
        }
    }


    public List<Map<String, String>> productInformation(String productId) {
        return productRepository.allInfo(productId);
    }

    public Map<String, String> productTitle(String productId) {
        return productRepository.productTitle(productId);
    }
}
