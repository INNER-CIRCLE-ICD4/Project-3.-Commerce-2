package org.icd4.commerce.common.idgenerator;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ConditionalOnClass({ULIDGenerator.class})
@ComponentScan(basePackages = "org.icd4.commerce")
public class HibernateIdGeneratorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ULIDGenerator ulidGenerator() {
        return new ULIDGenerator();
    }
}

