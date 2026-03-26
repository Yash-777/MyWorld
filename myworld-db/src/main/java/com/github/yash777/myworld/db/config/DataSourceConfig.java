package com.github.yash777.myworld.db.config;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
  entityManagerFactoryRef = "rdbmsEntityManager",
  transactionManagerRef = "transactionManager", // rdbmsTransactionManager
  basePackages = { "com.github.yash777.repositories", "com.github.yash777.*.repositories" }
)
/*
Exception in thread "main" java.lang.IllegalStateException: No CacheResolver specified, and no bean of type CacheManager found. Register a CacheManager bean or remove the @EnableCaching annotation from your configuration.
at org.springframework.cache.interceptor.CacheAspectSupport.afterSingletonsInstantiated(CacheAspectSupport.java:277)
at com.yash.db.config.DependencyScanner.main(DependencyScanner.java:40)
Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'org.springframework.cache.CacheManager' available
*/
//@org.springframework.cache.annotation.EnableCaching

//@ConditionalOnProperty(value = "datasourceType", havingValue = "RDBMS_MySQL", matchIfMissing = false)
//Exception in thread "main" org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'jpaBaseDaoImpl': Unsatisfied dependency expressed through field 'dataSource': No qualifying bean of type 'javax.sql.DataSource' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {@org.springframework.beans.factory.annotation.Autowired(required=true), @org.springframework.beans.factory.annotation.Qualifier("dataSource")}
//Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'dataSourceScriptDatabaseInitializer' defined in class path resource [org/springframework/boot/autoconfigure/sql/init/DataSourceInitializationConfiguration.class]: Unsatisfied dependency expressed through method 'dataSourceScriptDatabaseInitializer' parameter 0: Error creating bean with name 'dataSource' defined in class path resource [org/springframework/boot/autoconfigure/jdbc/DataSourceConfiguration$Hikari.class]: Failed to instantiate [com.zaxxer.hikari.HikariDataSource]: Factory method 'dataSource' threw exception with message: Failed to determine a suitable driver class
public class DataSourceConfig {
    //ehcache = Suppressed: java.lang.ClassNotFoundException: org.hibernate.cache.ehcache.EhCacheRegionFactory
    static String cachingProvider = "jcache"; // ehcache | jcache
    static boolean isHardCodedProps = true;
    
    {
        System.out.println("----- DataSourceConfig -----"); // myworld-db dependency added in api module.
    }
    static String entitiesPackage = "com.github.yash777.myworld";
    final static String dataSourcePrefix = "app.datasource.myapp";
    
    @Autowired
    Environment env;
    
    @Bean @Primary
    @ConfigurationProperties(prefix = "app.datasource.myapp")
    public DataSourceProperties dsproDataSourceProperties() {
        return new DataSourceProperties();
    }
    
    /*
<dependency>
<groupId>org.apache.tomcat:tomcat-jdbc</artifactId>
<version>${tomcat.version}</version>
</dependency>
     */
    @Bean(name = "dataSource")
    public DataSource tomcatDataSource() {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/flyway_demo?createDatabaseIfNotExist=true"); //("jdbc:mysql://localhost:3306/mydatabase");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUsername("root"); //("user");
        dataSource.setPassword("root"); //("password");
        return dataSource;
    }
/*
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>${hikaricp.version}</version>
</dependency>
 */
    @Bean(name = "dataSource") //@Primary
    public DataSource getHikariCP() {
        System.out.println("Creating dsproDataSource Bean......................");
        HikariConfig hikariConfig = new HikariConfig();
        
        if (isHardCodedProps) {
            hikariConfig.setJdbcUrl("jdbc:mysql://localhost:3306/myappschema?createDatabaseIfNotExist=true"); //("jdbc:mysql://localhost:3306/mydatabase");
            hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
            hikariConfig.setUsername("root"); //("user");
            hikariConfig.setPassword("root"); //("password");
            
            hikariConfig.setMaximumPoolSize(10); // Adjust based on your needs      100   / 10
            hikariConfig.setConnectionTimeout(30000); // Increase timeout if needed 10000 / 30000
            hikariConfig.setIdleTimeout(600000); // Adjust idle timeout             10000 / 600000
            hikariConfig.setMaxLifetime(1800000); // Adjust max lifetime            1000  / 1800000
            hikariConfig.setKeepaliveTime(1000); // MaxLifetime                     1000
            hikariConfig.setMinimumIdle(30);
        } else {
            hikariConfig.setJdbcUrl(env.getProperty("app.datasource.myapp.url",String.class));
            hikariConfig.setDriverClassName(env.getProperty("app.datasource.myapp.driver-class-name",String.class));
            hikariConfig.setUsername(env.getProperty("app.datasource.myapp.username",String.class));
            hikariConfig.setPassword(env.getProperty("app.datasource.myapp.password",String.class));
            
            hikariConfig.setMaximumPoolSize(env.getProperty("app.datasource.myapp.hikari.maximum-pool-size", Integer.class));
            hikariConfig.setConnectionTimeout(env.getProperty("app.datasource.myapp.hikari.connection-timeout", Long.class)); 
            hikariConfig.setIdleTimeout(env.getProperty("app.datasource.myapp.hikari.idle-timeout", Long.class)); 
            hikariConfig.setMaxLifetime(env.getProperty("app.datasource.myapp.hikari.max-lifetime", Long.class)); 
            hikariConfig.setKeepaliveTime(env.getProperty("app.datasource.myapp.hikari.max-lifetime", Long.class));
            hikariConfig.setMinimumIdle(env.getProperty("app.datasource.myapp.hikari.minimum-idle", Integer.class));
        }
        
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        return dataSource;
        // return dsproDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }
    
    @Bean @Primary
    public LocalContainerEntityManagerFactoryBean rdbmsEntityManager() {
        System.out.println("Creating rdbmsEntityManager Bean......................");
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource( getHikariCP() );
        em.setPackagesToScan( entitiesPackage );
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        //properties.put("hibernate.allow_update_outside_transaction",true);
        
/*
 * 
Caused by: java.lang.Throwable
    at org.hibernate.boot.registry.classloading.internal.AggregatedClassLoader.findClass(AggregatedClassLoader.java:209) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
Suppressed: java.lang.ClassNotFoundException: org.hibernate.dialect.MySQL5Dialect

Caused by: java.lang.ClassNotFoundException: Could not load requested class : org.hibernate.dialect.MySQL5Dialect
//properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
 * 
 * HHH90000026: MySQL8Dialect has been deprecated; use org.hibernate.dialect.MySQLDialect instead
 * o.h.o.deprecation : HHH90000025: MySQLDialect does not need to be specified explicitly using 'hibernate.dialect' (remove the property setting and it will be selected by default)
 */
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto")); // update
//        properties.put("hibernate.dialect", env.getProperty("hibernate.dialect")); // org.hibernate.dialect.MySQLDialect
        // Other Hibernate properties
//        properties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
//        properties.put("hibernate.format_sql", env.getProperty("hibernate.format_sql"));
        
// Cache configuration (Ensure that Ehcache is compatible with Hibernate 6.x)
// https://docs.jboss.org/hibernate/orm/5.6/userguide/html_single/Hibernate_User_Guide.html#configurations-cache
properties.put("hibernate.cache.use_second_level_cache", "true");
properties.put("hibernate.cache.use_query_cache", "true");
if (cachingProvider.equals("ehcache")) { 
// https://www.digitalocean.com/community/tutorials/hibernate-ehcache-hibernate-second-level-cache
// https://www.ehcache.org/documentation/2.8/integrations/hibernate.html#enable-second-level-cache-and-query-cache-settings
// Either a shortcut name (e.g. jcache, ehcache) or the fully-qualified name of the RegionFactory implementation class.
    properties.put("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory");
    //properties.put("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory");
// n.s.e.c.ConfigurationFactory : No configuration found. Configuring ehcache from ehcache-failsafe.xml  found in the classpath: jar:file:/C:/Users/ymerugu/.m2/repository/net/sf/ehcache/ehcache/2.10.9.2/ehcache-2.10.9.2.jar!/ehcache-failsafe.xml
    properties.put("net.sf.ehcache.configurationResourceName", "ehcache2.xml"); // EHCache configuration file location
} else { // EhcacheManager - Eh107CacheManager
    //org.hibernate.cache.internal.RegionFactoryInitiator -- HHH000025: Second-level cache region factory [org.hibernate.cache.jcache.internal.JCacheRegionFactory]
    properties.put("hibernate.cache.region.factory_class", "org.hibernate.cache.jcache.internal.JCacheRegionFactory");
    properties.put("hibernate.javax.cache.provider", "org.ehcache.jsr107.EhcacheCachingProvider");
    properties.put("hibernate.javax.cache.uri", "ehcache3-jsr107.xml");
}
        
/*
# HHH020100: The Ehcache second-level cache provider for Hibernate is deprecated.  See https://hibernate.atlassian.net/browse/HHH-12441 for details.
# Hibernate configuration for Ehcache integration
hibernate.cache.use_second_level_cache=true
hibernate.cache.use_query_cache=true

hibernate.cache.region.factory_class=org.hibernate.cache.ehcache.EhCacheRegionFactory
hibernate.cache.ehcache.config_file=classpath:ehcache.xml # (Ensure this file is correctly configured)

==============================================
properties.put("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory");

o.h.Version                              : HHH000412: Hibernate ORM core version 6.5.2.Final
2024-12-22T21:34:19.040+05:30 ERROR 22360 --- [           main] j.LocalContainerEntityManagerFactoryBean : Failed to initialize JPA EntityManagerFactory: Unable to create requested service [org.hibernate.cache.spi.RegionFactory] due to: Caching was explicitly requested, but no RegionFactory was defined and there is not a single registered RegionFactory
2024-12-22T21:34:19.042+05:30  WARN 22360 --- [           main] ConfigServletWebServerApplicationContext : Exception encountered during context initialization - cancelling refresh attempt: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'rdbmsEntityManager' defined in class path resource [com/yash/db/config/DataSourceConfig.class]: Unable to create requested service [org.hibernate.cache.spi.RegionFactory] due to: Caching was explicitly requested, but no RegionFactory was defined and there is not a single registered RegionFactory
2024-12-22T21:34:19.043+05:30  INFO 22360 --- [           main] c.z.h.HikariDataSource                   : HikariPool-1 - Shutdown initiated...
2024-12-22T21:34:19.080+05:30  INFO 22360 --- [           main] c.z.h.HikariDataSource                   : HikariPool-1 - Shutdown completed.
==============================================

hibernate.cache.region.factory_class=org.hibernate.cache.jcache.internal.JCacheRegionFactory
hibernate.javax.cache.uri=ehcache.xml
hibernate.javax.cache.provider=org.ehcache.jsr107.EhcacheCachingProvider

==============================================
//        properties.put("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory");

o.h.Version                              : HHH000412: Hibernate ORM core version 6.5.2.Final
2024-12-22T21:25:53.584+05:30  INFO 35220 --- [           main] o.h.c.i.RegionFactoryInitiator           : HHH000025: Second-level cache region factory [org.hibernate.cache.jcache.internal.JCacheRegionFactory]
2024-12-22T21:25:54.979+05:30  INFO 35220 --- [           main] o.s.o.j.p.SpringPersistenceUnitInfo      : No LoadTimeWeaver setup: ignoring JPA class transformer
2024-12-22T21:25:55.215+05:30  WARN 35220 --- [           main] o.h.o.deprecation                        : HHH90000025: MySQL8Dialect does not need to be specified explicitly using 'hibernate.dialect' (remove the property setting and it will be selected by default)
2024-12-22T21:25:55.218+05:30  WARN 35220 --- [           main] o.h.o.deprecation                        : HHH90000026: MySQL8Dialect has been deprecated; use org.hibernate.dialect.MySQLDialect instead
2024-12-22T21:25:56.617+05:30  WARN 35220 --- [           main] o.h.o.cache                              : HHH90001006: Missing cache[default-update-timestamps-region] was created on-the-fly. The created cache will use a provider-specific default configuration: make sure you defined one. You can disable this warning by setting 'hibernate.javax.cache.missing_cache_strategy' to 'create'.
2024-12-22T21:25:56.848+05:30  INFO 35220 --- [           main] o.e.c.EhcacheManager                     : Cache 'default-update-timestamps-region' created in EhcacheManager.
2024-12-22T21:25:56.875+05:30  WARN 35220 --- [           main] o.h.o.cache                              : HHH90001006: Missing cache[default-query-results-region] was created on-the-fly. The created cache will use a provider-specific default configuration: make sure you defined one. You can disable this warning by setting 'hibernate.javax.cache.missing_cache_strategy' to 'create'.
2024-12-22T21:25:56.881+05:30  INFO 35220 --- [           main] o.e.c.EhcacheManager                     : Cache 'default-query-results-region' created in EhcacheManager.
2024-12-22T21:25:56.915+05:30  WARN 35220 --- [           main] o.h.o.cache                              : HHH90001006: Missing cache[com.yash.db.entities.Address] was created on-the-fly. The created cache will use a provider-specific default configuration: make sure you defined one. You can disable this warning by setting 'hibernate.javax.cache.missing_cache_strategy' to 'create'.
2024-12-22T21:25:56.920+05:30  INFO 35220 --- [           main] o.e.c.EhcacheManager                     : Cache 'com.yash.db.entities.Address' created in EhcacheManager.
2024-12-22T21:25:58.452+05:30  INFO 35220 --- [           main] o.h.e.t.j.p.i.JtaPlatformInitiator       : HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
Hibernate: 
    alter table User 
       modify column Definition text not null */
        
        em.setJpaPropertyMap(properties);
        return em;
    }
    
    @Bean @Primary
    public PlatformTransactionManager transactionManager() {
        //public PlatformTransactionManager rdbmsTransactionManager() {
        System.out.println("Creating Transaction Mangaer..................");
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory( rdbmsEntityManager().getObject() );
        return transactionManager;
    }
    
    
    @Bean
    public JdbcTemplate jdbcTemplate() {
        System.out.println("Creating jdbcTemplate ..................");
        return new JdbcTemplate( getHikariCP() );
    }
    
    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        System.out.println("Creating namedParameterJdbcTemplate ..................");
        return new NamedParameterJdbcTemplate( getHikariCP() );
    }
}