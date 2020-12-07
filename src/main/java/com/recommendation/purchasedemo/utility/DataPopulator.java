package com.recommendation.purchasedemo.utility;

import com.recommendation.purchasedemo.service.File2RepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@ConditionalOnProperty(name = {"app.mock.data.path"})
public class DataPopulator {

    @Autowired
    File2RepositoryService file2RepositoryService;

    @Value("${app.mock.data.path:classpath*:staged-data/*.json}")
    String location;

    boolean shouldOverWrite = true;


    @PostConstruct
    public void populate() throws IOException {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        List<Resource> resources = Arrays.asList(resourcePatternResolver.getResources(location));

        resources.parallelStream().forEach(resource -> {
            try {
                String fullName = FilenameUtils.getName(resource.getFilename());
                String collectionName = FilenameUtils.removeExtension(fullName);
                log.info("collectionName={} fullName={} resource={}", collectionName, fullName, resource);
                file2RepositoryService.uploadRows(collectionName, resource.getInputStream(), shouldOverWrite);
            } catch (IOException e) {
                log.warn("Failed to load data set : ", e);
            }
        });
    }
}
