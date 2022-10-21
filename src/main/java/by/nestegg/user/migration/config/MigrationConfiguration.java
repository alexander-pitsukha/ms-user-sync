package by.nestegg.user.migration.config;

import by.nestegg.user.migration.config.properties.MigrationDatasourceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
@EnableJpaRepositories(basePackages = MigrationConfiguration.MIGRATION_PACKAGE_NAME,
        entityManagerFactoryRef = MigrationConfiguration.MIGRATION_ENTITY_MANAGER,
        transactionManagerRef = MigrationConfiguration.MIGRATION_TRANSACTION_MANAGER)
@EnableConfigurationProperties(MigrationDatasourceProperties.class)
public class MigrationConfiguration {

    public static final String MIGRATION_DATA_SOURCE = "migrationDataSource";
    public static final String MIGRATION_ENTITY_MANAGER = "migrationEntityManager";
    public static final String MIGRATION_TRANSACTION_MANAGER = "migrationTransactionManager";
    public static final String MIGRATION_PACKAGE_NAME = "by.nestegg.user.migration.persistence";

    @Bean(MIGRATION_ENTITY_MANAGER)
    @Primary
    @Autowired
    public LocalContainerEntityManagerFactoryBean migrationEntityManager(
            @Qualifier(MIGRATION_DATA_SOURCE) DataSource dataSource, Environment environment) {
        var entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPackagesToScan(MIGRATION_PACKAGE_NAME);
        var vendorAdapter = new HibernateJpaVendorAdapter();
        entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", environment.getProperty("spring.jpa.hibernate.ddl-auto"));
        properties.put("hibernate.dialect", environment.getProperty("spring.migration.hibernate.dialect"));
        entityManagerFactoryBean.setJpaPropertyMap(properties);
        return entityManagerFactoryBean;
    }

    @Bean(MIGRATION_DATA_SOURCE)
    @Primary
    @Autowired
    public DataSource migrationDataSource(MigrationDatasourceProperties migrationDatasourceProperties) {
        var dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(migrationDatasourceProperties.getDriverClassName());
        dataSource.setUrl(migrationDatasourceProperties.getUrl());
        dataSource.setUsername(migrationDatasourceProperties.getUsername());
        dataSource.setPassword(migrationDatasourceProperties.getPassword());
        return dataSource;
    }

    @Bean(MIGRATION_TRANSACTION_MANAGER)
    @Primary
    @Autowired
    public PlatformTransactionManager migrationTransactionManager(
            @Qualifier(MIGRATION_ENTITY_MANAGER) LocalContainerEntityManagerFactoryBean entityManager) {
        var transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManager.getObject());
        return transactionManager;
    }

}
