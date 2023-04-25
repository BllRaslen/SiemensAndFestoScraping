package com.festo.scrapingfesoproducts;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@SpringBootApplication
public class ScrapingFesoProductsApplication {

    public static void main(String[] args) throws IOException {
          SpringApplication.run(ScrapingFesoProductsApplication.class, args);
    }
}
