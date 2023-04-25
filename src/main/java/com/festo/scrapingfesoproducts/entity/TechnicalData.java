package com.festo.scrapingfesoproducts.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "technical_data")
public class TechnicalData {

    @Id
    @SequenceGenerator(name = "sequence_technical_data",
            sequenceName = "sequence_technical_data",
            allocationSize = 1,
            initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sequence_technical_data")
    @Column(name = "technical_data_id")
    private Long technicalDataId;

    @Column(name = "key")
    private String key;
    @Column(name = "value")
    private String value;


    @Column(name = "product_id_fk", updatable = false, insertable = false)
    private String productIdFk;

    ///////////////

    public String getProductIdFk() {
        return productIdFk;
    }

    public void setProductIdFk(String productIdFk) {

        this.productIdFk =productIdFk;
    }

    ///////////////

  /*  private String productName;

    private String productCode;*/


    @ManyToOne
    @JoinColumn(name = "product_id_fk", referencedColumnName = "product_id")
    private Product product;





    public Long getTechnicalDataId() {
        return technicalDataId;
    }

    public void setTechnicalDataId(Long technicalDataId) {
        this.technicalDataId = technicalDataId;
    }

    public String getName() {
        return key;
    }

    public void setName(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


}