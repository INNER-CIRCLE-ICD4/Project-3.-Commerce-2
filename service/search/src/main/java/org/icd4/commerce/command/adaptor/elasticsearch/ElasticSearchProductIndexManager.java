package org.icd4.commerce.command.adaptor.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.command.application.required.IndexManager;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ElasticSearchProductIndexManager implements IndexManager {

    private final ElasticsearchClient esClient;

    @Override
    public void createIndex() throws IOException {
        // es index_template 생성/ index_schema 관리 등등
        // -> 근데 어플리케이션에서 하기보다 스키마 파일을 관리하거나 외부에서 하는게 어떨까 생각합니다
    }

    @Override
    public void deleteIndex() throws IOException {
       //
    }
}
