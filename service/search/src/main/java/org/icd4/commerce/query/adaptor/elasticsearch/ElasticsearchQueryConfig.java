package org.icd4.commerce.query.adaptor.elasticsearch;

import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

// Elasticsearch 클라이언트 연결
public class ElasticsearchQueryConfig extends ElasticsearchConfiguration {

    @Override
    public ClientConfiguration clientConfiguration() {
        return null;
    }
}
