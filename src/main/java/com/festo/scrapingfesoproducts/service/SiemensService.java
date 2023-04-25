package com.festo.scrapingfesoproducts.service;

import com.festo.scrapingfesoproducts.entity.Product;
import com.festo.scrapingfesoproducts.entity.TechnicalData;
import com.festo.scrapingfesoproducts.repository.ProductRepository;
import com.festo.scrapingfesoproducts.repository.TechnicalDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class SiemensService {
    //private final LanguageManipulation languageManipulation;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TechnicalDataRepository technicalDataRepository;


    @Async("asyncExecutor")
    public CompletableFuture<JSONObject> scrape(String productCode) throws IOException, InterruptedException {

        // log.debug("SiemensService1 has been started in {}", languageManipulation.convert(language));

        Document doc = null;
        try {
            doc = Jsoup.connect("https://mall.industry.siemens.com/mall/en/uk/Catalog/Product/?mlfb=" + productCode).get();
        } catch (Exception e) {
            log.warn("Exception from Siemens{}", e.getMessage());
            return null;
        }

        JSONObject scrapedProduct = new JSONObject();
        JSONArray eclassProperties = new JSONArray();
        Product product = new Product();

        try {
            scrapedProduct.put("company", "Siemens");
            if (!(doc.select("div.Breadcrumb > div:nth-child(4) > a").text().isEmpty())) {
                final var name = scrapedProduct.put("name", doc.select("div.Breadcrumb > div:nth-child(4) > a").text());

                product.setProductName(name.toString());
            } else
                scrapedProduct.put("name", " ");
            if (doc.select("span.productIdentifier").first() != null) {
                final var productcode = scrapedProduct.put("productcode", doc.select("span.productIdentifier").first().text());
                product.setProductId(productCode);
            } else
                scrapedProduct.put("productcode", " ");

            scrapedProduct.put("site", "https://mall.industry.siemens.com/mall/en/uk/Catalog/Product/" + productCode);
            scrapedProduct.put("datasheet", "https://mall.industry.siemens.com" + doc.select("a.pdfLink").attr("href"));

            Elements eClassRows = doc.select("#content > table > tbody > tr > td.ProductDetails > div:nth-child(6) > div > div > table > tbody > tr.productDetailsTable_ClassificationRow > td:nth-child(2) > table > tbody > tr");

            for (int i = 0; i < eClassRows.size(); i++) {
                JSONObject eclassPropertie = new JSONObject();
                if (eClassRows.get(i).select("td:nth-child(1)").text().equals("eClass")) {
                    eclassPropertie.put("name", "ECLASS-" + eClassRows.get(i).select("td:nth-child(2)").text());
                    eclassPropertie.put("value", eClassRows.get(i).select("td:nth-child(3)").text());
                    eclassProperties.put(eclassPropertie);
                }
            }
            //scrapedProduct.put("eClass",eclassProperties);
            String categoryString = "";
            //Elements categoryElements = doc.select("body > div.headerandcontent > div.nwaContentHeader > div > div > div.col-md.order-1.order-md-0 > div");
            Elements categoryElements = doc.select("body > div.headerandcontent > div.nwaContentHeader > div > div > div.col-md.order-1.order-md-0> div.Breadcrumb> div.Item");
            //log.debug("....:{}",categoryElements.text());
            for (int i = 0; i < categoryElements.size(); i++) {
                //log.debug("category:{}.{}",i,categoryElements.get(i).getElementsByAttribute("id").text());
                categoryString += categoryElements.get(i).getElementsByAttribute("id").text() + ";";

            }
            scrapedProduct.put("category", categoryString);


        } catch (Exception e) {
            log.debug("Exception from Siemens:{}", e.getMessage());
            return null;
        }

        //Thread.sleep(1000);
        try {
            URL url = new URL("https://mall.industry.siemens.com/mall/en/UK/Catalog/Product/?mlfb=" + productCode);
            System.out.println(url);
            InputStream in = null;
            try {
                //url.openConnection();
                in = new BufferedInputStream(url.openStream());
            } catch (MalformedURLException e) {
                log.debug("SiemensService1 pdf reading:{}", e.getMessage());
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[131072];
            int n = 0;
            while (-1 != (n = in.read(buf))) {
                out.write(buf, 0, n);

            }
            out.close();
            in.close();
            byte[] response = out.toByteArray();
            response[0] = 3;
            //FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir") + "/siemens_tmp.pdf");
            final var x = new String(response, StandardCharsets.UTF_8);
            FileOutputStream fos = new FileOutputStream("C:\\Users\\bilal\\OneDrive\\Masaüstü\\festo\\siemens_tmp.pdf");
            int i = 0;
            fos.write(response[i++]);
            fos.close();
        } catch (Exception e) {
            log.debug("Siemens Page scraper failed to download PDF file" + e);
            return null;
        }

        PDDocument pd = null;
        try {
            //final String FILENAME = System.getProperty("user.dir") + "/siemens_tmp.pdf";
            final String FILENAME = "C:\\Users\\bilal\\OneDrive\\Masaüstü\\festo\\22.pdf";

            pd = Loader.loadPDF(new File(FILENAME));

            int totalPages = pd.getNumberOfPages();
            log.debug("Total Pages in Document: " + totalPages);
            System.out.println("totalPages = " + totalPages);

            ObjectExtractor oe = new ObjectExtractor(pd);
            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
            List<Page> pages = new ArrayList<Page>();
            for (int i = 1; i <= totalPages; i++) {
                pages.add(oe.extract(i));
            }
            Long ii = 0L;
            List<TechnicalData> productFeatures = new ArrayList<>();
            JSONArray attributes = new JSONArray();
            for (Page page : pages) {
                // extract text from the table after detecting
                List<Table> table = sea.extract(page);
                for (Table tables : table) {
                    var rows = tables.getRows();

                    for (int i = 0; i < rows.size(); i++) {

                        var cells = rows.get(i);

                        if (cells.size() > 1 && cells.get(1).getText() != "") {
                            TechnicalData technicalData = new TechnicalData();
                            JSONObject attribute = new JSONObject();
                            attribute.put("name", (cells.get(0).getText() != "" ? cells.get(0).getText().replace("●", "") : "item description"));
                            attribute.put("value", cells.get(1).getText().replace("●", ""));

                            String name = attribute.getString("name");
                            String value = attribute.getString("value");

                            technicalData.setTechnicalDataId( ii);
                            ii++;
                            technicalData.setName(name);
                            technicalData.setValue(value);
                            technicalData.setProduct(product);
                            productFeatures.add(technicalData);


                            attributes.put(attribute);
                        }
                    }
                }
            }
            pd.close();
            oe.close();
            productRepository.save(product);
            technicalDataRepository.saveAll(productFeatures);
            scrapedProduct.put("properties", attributes);

            for (int i = 0; i < eclassProperties.length(); i++) {
                JSONObject property = (JSONObject) eclassProperties.get(i);
                scrapedProduct.getJSONArray("properties").put(property);
            }

        } catch (Exception e) {
            if (pd != null) {
                pd.close();
            }
            log.debug("(PDFBOX) failed to parse pdf data!" + e);
            return null;
        }
        return CompletableFuture.completedFuture(scrapedProduct);
    }

    public List<Map<String, String>> productInformation(String productId) {
        return productRepository.allInfo(productId);
    }

    public Map<String, String> productTitle(String productId) {
        return productRepository.productTitle(productId);
    }
}
