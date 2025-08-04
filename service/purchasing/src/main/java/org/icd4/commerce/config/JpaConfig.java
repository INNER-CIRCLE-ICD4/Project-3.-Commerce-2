package org.icd4.commerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "org.icd4.commerce.adapter.persistence")
@EnableTransactionManagement
public class JpaConfig {
}