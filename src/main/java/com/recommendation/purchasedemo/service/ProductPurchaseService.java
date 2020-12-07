package com.recommendation.purchasedemo.service;

import com.recommendation.purchasedemo.storage.Product;
import com.recommendation.purchasedemo.storage.ProductPurchase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.RequestMethod;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.FacetFieldEntry;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductPurchaseService {

    @Autowired
    SolrTemplate solrTemplate;

    final int topFrequency = 5;

    public List<Product> getRecommendation(String productId) {
        //Customers buying same product
        Criteria queryCriteria = Criteria.where(ProductPurchase.PRODUCT_ID).is(productId);
        Query query = new SimpleQuery(queryCriteria);
        query.setRows(17000);
        query.addFilterQuery(new SimpleFilterQuery(new SimpleStringCriteria("{!collapse field=" + ProductPurchase.CUSTOMER_ID + "}")));
        Page<ProductPurchase> records = solrTemplate.query("productpurchase", query, ProductPurchase.class,
                RequestMethod.POST);
        List<String> customers = records.getContent().stream().map(ProductPurchase::getCustomerId).collect(Collectors.toList());

        //Top products bought along
        FacetOptions facetOptions = new FacetOptions()
                .addFacetOnField(ProductPurchase.PRODUCT_ID).setFacetLimit(topFrequency);
        Criteria queryCriteriaForFacet = Criteria.where(ProductPurchase.PRODUCT_ID).is(productId).not()
                .and(ProductPurchase.CUSTOMER_ID).in(customers);
        FacetQuery facetQuery = new SimpleFacetQuery(Objects.requireNonNull(queryCriteriaForFacet)).setFacetOptions(facetOptions);
        facetQuery.setRows(0);
        List<FacetFieldEntry> facetPage = solrTemplate.queryForFacetPage("productpurchase", facetQuery, ProductPurchase.class, RequestMethod.POST)
                .getFacetResultPage(ProductPurchase.PRODUCT_ID).getContent();
        List<String> topProductIds = facetPage.stream().map(FacetFieldEntry::getValue).collect(Collectors.toList());

        //Product with name
        Criteria queryCriteriaProduct = Criteria.where(Product.PRODUCT_ID).in(topProductIds);
        Query queryProduct = new SimpleQuery(queryCriteriaProduct);
        query.setRows(topFrequency);
        Page<Product> products = solrTemplate.query("product", queryProduct, Product.class,
                RequestMethod.POST);
        return products.getContent();
    }
}
