package com.recommendation.purchasedemo.storage;

import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends SolrCrudRepository<Product, String> {
}
