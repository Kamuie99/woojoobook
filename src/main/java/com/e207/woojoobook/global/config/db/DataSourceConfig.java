package com.e207.woojoobook.global.config.db;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.EntityManagerFactory;

// @Configuration
// @EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
// @EnableConfigurationProperties(DataSourceProperties.class)
public class DataSourceConfig {
	private final DataSourceProperties databaseProperty;
	private final JpaProperties jpaProperties;

	public DataSourceConfig(DataSourceProperties databaseProperty, JpaProperties jpaProperties) {
		this.databaseProperty = databaseProperty;
		this.jpaProperties = jpaProperties;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		EntityManagerFactoryBuilder entityManagerFactoryBuilder = createEntityManagerFactoryBuilder(jpaProperties);
		return entityManagerFactoryBuilder.dataSource(dataSource()).packages("com.e207.woojoobook").build();
	}

	@Bean
	public DataSource dataSource() {
		return new LazyConnectionDataSourceProxy(routingDataSource());
	}

	@Bean
	public DataSource routingDataSource() {
		DataSource master = createDataSource(
			databaseProperty.getUrl(),
			databaseProperty.getUsername(),
			databaseProperty.getPassword()
		);

		Map<Object, Object> dataSourceMap = new LinkedHashMap<>();
		dataSourceMap.put("master", master);
		databaseProperty.getSlave()
			.forEach((key, value) -> dataSourceMap.put(
				value.getName(),
				createDataSource(
					value.getUrl(),
					value.getUsername(),
					value.getPassword()
				)
			));

		ReplicationRoutingDataSource replicationRoutingDataSource = new ReplicationRoutingDataSource();
		replicationRoutingDataSource.setDefaultTargetDataSource(master);
		replicationRoutingDataSource.setTargetDataSources(dataSourceMap);
		return replicationRoutingDataSource;
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager tm = new JpaTransactionManager();
		tm.setEntityManagerFactory(entityManagerFactory);
		return tm;
	}

	public DataSource createDataSource(String url, String username, String password) {
		return DataSourceBuilder.create()
			.type(HikariDataSource.class)
			.url(url)
			.driverClassName("com.mysql.cj.jdbc.Driver")
			.username(username)
			.password(password)
			.build();
	}

	private EntityManagerFactoryBuilder createEntityManagerFactoryBuilder(JpaProperties jpaProperties) {
		AbstractJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		return new EntityManagerFactoryBuilder(vendorAdapter, jpaProperties.getProperties(), null);
	}
}
