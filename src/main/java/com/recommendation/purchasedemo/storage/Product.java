package com.recommendation.purchasedemo.storage;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Builder;
import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(collection = "product")
@Data
@Builder
public class Product {

    public static final String PRODUCT_ID = "id";
    @Field(PRODUCT_ID)
    String productId;
    public static final String PRODUCT_NAME = "productname_s";
    @Field(PRODUCT_NAME)
    String productName;
}
