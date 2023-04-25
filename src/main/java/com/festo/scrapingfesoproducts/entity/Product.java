package com.festo.scrapingfesoproducts.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @Column(name = "product_id")
    private String productId;
    @Column()
    private String productName;
    @Column()
    private String productCode;
    @Column
    private String productProducer;


    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TechnicalData> technicalDataList;


    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public List<TechnicalData> getTechnicalDataList() {
        return technicalDataList;
    }

    public void setTechnicalDataList(List<TechnicalData> technicalDataList) {
        this.technicalDataList = technicalDataList;
    }

    public String getProductProducer() {
        return productProducer;
    }

    public void setProductProducer(String productProducer) {
        this.productProducer = productProducer;
    }
}


