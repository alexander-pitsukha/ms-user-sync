package by.nestegg.user.migration.config;

import by.nestegg.user.migration.config.properties.ClientDatasourceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(ClientDatasourceProperties.class)
@EnableJpaRepositories(basePackages = ClientConfiguration.CLIENT_BASE_PACKAGE,
        entityManagerFactoryRef = ClientConfiguration.CLIENT_ENTITY_MANAGER,
        transactionManagerRef = ClientConfiguration.CLIENT_TRANSACTION_MANAGER)
public class ClientConfiguration {

    public static final String CLIENT_DATA_SOURCE = "clientDataSource";
    public static final String CLIENT_ENTITY_MANAGER = "clientEntityManager";
    public static final String CLIENT_TRANSACTION_MANAGER = "clientTransactionManager";
    public static final String CLIENT_BASE_PACKAGE = "by.nestegg.user.migration.client";

    @Bean(CLIENT_ENTITY_MANAGER)
    @Autowired
    public LocalContainerEntityManagerFactoryBean clientEntityManager(
            @Qualifier(CLIENT_DATA_SOURCE) DataSource dataSource, Environment environment) {
        var entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPackagesToScan(CLIENT_BASE_PACKAGE);
        var vendorAdapter = new HibernateJpaVendorAdapter();
        entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", environment.getProperty("spring.jpa.hibernate.ddl-auto"));
        properties.put("hibernate.dialect", environment.getProperty("spring.client.hibernate.dialect"));
        entityManagerFactoryBean.setJpaPropertyMap(properties);
        return entityManagerFactoryBean;
    }

    @Bean(CLIENT_DATA_SOURCE)
    @Autowired
    public DataSource clientDataSource(ClientDatasourceProperties clientDatasourceProperties) {
        var dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(clientDatasourceProperties.getDriverClassName());
        dataSource.setUrl(clientDatasourceProperties.getUrl());
        dataSource.setUsername(clientDatasourceProperties.getUsername());
        dataSource.setPassword(clientDatasourceProperties.getPassword());
        return dataSource;
    }

    @Bean(CLIENT_TRANSACTION_MANAGER)
    @Autowired
    public PlatformTransactionManager clientTransactionManager(
            @Qualifier(CLIENT_ENTITY_MANAGER) LocalContainerEntityManagerFactoryBean entityManager) {
        var transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManager.getObject());
        return transactionManager;
    }

}
