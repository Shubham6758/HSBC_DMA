package com.coforge.hsbcdma.config.database;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages =
                "com.coforge.hsbcdma.revenuemargin.repository",
        entityManagerFactoryRef =
                "revenueEntityManagerFactory",
        transactionManagerRef =
                "revenueTransactionManager"
)
public class RevenueDatabaseConfig {

    @Bean(name = "revenueDataSourceProperties")
    @ConfigurationProperties(prefix = "app.datasource.revenue")
    public DataSourceProperties revenueDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "revenueDataSource")
    public DataSource revenueDataSource(
            @Qualifier("revenueDataSourceProperties")
            DataSourceProperties properties) {

        return properties
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean(name = "revenueEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean
    revenueEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("revenueDataSource")
            DataSource dataSource) {

        Map<String, Object> jpaProperties = new HashMap<>();

        jpaProperties.put(
                "hibernate.hbm2ddl.auto",
                "update"
        );

        jpaProperties.put(
                "hibernate.dialect",
                "org.hibernate.dialect.MySQLDialect"
        );

        jpaProperties.put(
                "hibernate.jdbc.time_zone",
                "Asia/Kolkata"
        );

        jpaProperties.put(
                "hibernate.show_sql",
                true
        );

        jpaProperties.put(
                "hibernate.format_sql",
                true
        );

        return builder
                .dataSource(dataSource)
                .packages(
                        "com.coforge.hsbcdma.revenuemargin.entity"
                )
                .persistenceUnit("revenuePersistenceUnit")
                .properties(jpaProperties)
                .build();
    }

    @Bean(name = "revenueTransactionManager")
    public PlatformTransactionManager revenueTransactionManager(
            @Qualifier("revenueEntityManagerFactory")
            EntityManagerFactory entityManagerFactory) {

        return new JpaTransactionManager(entityManagerFactory);
    }
}
