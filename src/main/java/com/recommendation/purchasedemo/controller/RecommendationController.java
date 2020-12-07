package com.recommendation.purchasedemo.controller;

import com.recommendation.purchasedemo.service.ProductPurchaseService;
import com.recommendation.purchasedemo.storage.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/core-app-api/recommendation", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class RecommendationController {

    @Autowired
    ProductPurchaseService productPurchaseService;

    @RequestMapping(value = "/product/{productId}", method = RequestMethod.GET)
    public ResponseEntity<List<Product>> getRecommendation(@PathVariable String productId) {
        return ResponseEntity.ok(productPurchaseService.getRecommendation(productId));
    }
}
