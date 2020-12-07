package com.recommendation.purchasedemo.storage;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Builder;
import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(collection = "productpurchase")
@Data
@Builder
public class ProductPurchase {
    @Field
    @JsonSerialize(using = ToStringSerializer.class)
    Long id;
    public static final String PRODUCT_ID = "productid_s";
    @Field(PRODUCT_ID)
    String productId;
    public static final String PRODUCT_NAME = "productname_s";
    @Field(PRODUCT_NAME)
    String productName;
    public static final String CUSTOMER_ID = "customerid_s";
    @Field(CUSTOMER_ID)
    String customerId;
}
