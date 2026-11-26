package com.coforge.hsbcdma.config.database;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = {
                "com.coforge.hsbcdma"
        },
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.REGEX,
                        pattern = "com\\.coforge\\.hsbcdma\\.revenuemargin\\..*"
                )
        },
        entityManagerFactoryRef = "dmaEntityManagerFactory",
        transactionManagerRef = "dmaTransactionManager"
)
public class DmaDatabaseConfig {

    @Primary
    @Bean(name = "dmaDataSourceProperties")
    @ConfigurationProperties("app.datasource.dma")
    public DataSourceProperties dmaDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "dmaDataSource")
    public DataSource dmaDataSource(
            @Qualifier("dmaDataSourceProperties")
            DataSourceProperties properties) {

        return properties
                .initializeDataSourceBuilder()
                .build();
    }

    @Primary
    @Bean(name = "dmaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean dmaEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("dmaDataSource") DataSource dataSource) {

        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");
        jpaProperties.put(
                "hibernate.dialect",
                "org.hibernate.dialect.MySQLDialect"
        );
        jpaProperties.put(
                "hibernate.jdbc.time_zone",
                "Asia/Kolkata"
        );
        jpaProperties.put("hibernate.show_sql", true);
        jpaProperties.put("hibernate.format_sql", true);

        return builder
                .dataSource(dataSource)
                .packages("com.coforge.hsbcdma")
                .persistenceUnit("dmaPersistenceUnit")
                .properties(jpaProperties)
                .build();
    }

    @Primary
    @Bean(name = "dmaTransactionManager")
    public PlatformTransactionManager dmaTransactionManager(
            @Qualifier("dmaEntityManagerFactory")
            EntityManagerFactory entityManagerFactory) {

        return new JpaTransactionManager(entityManagerFactory);
    }
}