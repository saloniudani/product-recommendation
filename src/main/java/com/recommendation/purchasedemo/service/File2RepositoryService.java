package com.recommendation.purchasedemo.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recommendation.purchasedemo.utility.UniqueId;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class File2RepositoryService {
    @Autowired
    SolrTemplate solrTemplate;
    @Autowired
    ObjectMapper objectMapper;

    Integer batchSize = 1000;

    Map<String, Long> totalUploaded = new ConcurrentHashMap<>();

    public void uploadRows(String collectionName, InputStream file, boolean shouldOverwrite) {
        log.debug("Batch size {}", batchSize);
        if (shouldOverwrite) {
            JsonFactory jsonFactory = new JsonFactory();
            try (JsonParser jsonParser = jsonFactory.createParser(file)) {
                JsonToken jsonToken = jsonParser.nextToken();
                if (jsonToken == null) {
                    return;
                }

                if (!JsonToken.START_ARRAY.equals(jsonToken)) {
                    return;
                }
                AtomicLong count = new AtomicLong();
                long currentCount;
                List<SolrInputDocument> solrInputDocuments = new ArrayList<>();
                while (true) {
                    try {
                        jsonToken = jsonParser.nextToken();

                        if (jsonToken == null) {
                            break;
                        }
                        if (!JsonToken.START_OBJECT.equals(jsonToken)) {
                            break;
                        }
                        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {
                        };
                        Map<String, Object> jsonObject = objectMapper.readValue(jsonParser, typeReference);
                        currentCount = count.incrementAndGet();

                        SolrInputDocument solrInputDocument = new SolrInputDocument();

                        if (!jsonObject.containsKey("id")) {
                            jsonObject.put("id", String.valueOf(UniqueId.GENERATOR.generate()));
                        }

                        jsonObject.forEach((name, value) -> {
                            solrInputDocument.addField(name, value);
                        });
                        solrInputDocuments.add(solrInputDocument);
                        if (currentCount % batchSize == 0) {
                            UpdateResponse updateResponse = solrTemplate.saveDocuments(collectionName, solrInputDocuments);
                            solrTemplate.commit(collectionName);
                            log.debug("UpdateResponse {}", updateResponse);
                            solrInputDocuments.clear();
                        }

                    } catch (IOException e) {
                        log.error("Data population for a batch of {} failed.", file, e);
                    }
                }
                if (!solrInputDocuments.isEmpty()) {
                    UpdateResponse updateResponse = solrTemplate.saveDocuments(collectionName, solrInputDocuments);
                    solrTemplate.commit(collectionName);
                    log.debug("{} UpdateResponse {}", collectionName, updateResponse);
                }

                log.info("{} Uploaded rows = {}", collectionName, count.get());
                totalUploaded.put(collectionName, count.get());
            } catch (IOException e) {
                log.error("Data population failed for {}.", file, e);
            }
        } else {
            log.info("collectionName={} shouldOverwrite={}", collectionName, shouldOverwrite);
        }
    }
}
