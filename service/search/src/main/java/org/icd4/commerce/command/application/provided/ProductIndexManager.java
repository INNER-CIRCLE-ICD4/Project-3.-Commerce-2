package org.icd4.commerce.command.application.provided;

import java.io.IOException;

// 엘라스틱서치 인덱스 관리 인터페이스
public interface ProductIndexManager {
    void createIndex() throws IOException;
    void deleteIndex() throws IOException;
}
