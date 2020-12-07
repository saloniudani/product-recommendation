package com.recommendation.purchasedemo.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Paths;

@Configuration
@EnableSolrRepositories(basePackages = "com.recommendation.purchasedemo.storage")
public class AppSolrConfig {
    @Bean
    public SolrClient embeddedSolrServer() throws IOException, SAXException, ParserConfigurationException {
        return new EmbeddedSolrServer(Paths.get("solr/embedded"),"productpurchase");
    }

    @Bean
    public SolrTemplate solrTemplate() throws ParserConfigurationException, SAXException, IOException {
        return new SolrTemplate(embeddedSolrServer());
    }
}
