/*
 *
 *
 *  * Copyright (c) 2017 Leo Lee(lichl.1980@163.com).
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy
 *  * of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations
 *  * under the License.
 *  *
 *
 */

package org.sunyata.quark.server.springcloud.store;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

@Configuration
@MapperScan(basePackages = AdsDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "adsSqlSessionFactory")
public class AdsDataSourceConfig {
    static final String PACKAGE = "org.sunyata.quark.server.springcloud.store";

    @Value("${custom.datasource.url}")
    private String dbUrl;
    @Value("${custom.datasource.username}")
    private String dbUser;
    @Value("${custom.datasource.password}")
    private String dbPassword;

    @Bean(name = "adsDataSource")
    public DataSource adsDataSource() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUser);
        dataSource.setPassword(dbPassword);

        dataSource.setInitialSize(1);
        dataSource.setMaxActive(20);

        dataSource.setMaxWait(60000);
        dataSource.setMinIdle(1);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(20);
        dataSource.setFilters("stat,wall,log4j");
        //dataSource.setMaxPoolPreparedStatementPerConnectionSize(-1);

        return dataSource;
        /**
         *      <property name="maxActive" value="20" />
         <property name="initialSize" value="1" />
         <property name="maxWait" value="60000" />
         <property name="minIdle" value="1" />

         <property name="timeBetweenEvictionRunsMillis" value="60000" />
         <property name="minEvictableIdleTimeMillis" value="300000" />

         <property name="testWhileIdle" value="true" />
         <property name="testOnBorrow" value="false" />
         <property name="testOnReturn" value="false" />

         <property name="poolPreparedStatements" value="true" />
         <property name="maxOpenPreparedStatements" value="20" />
         */
    }

    @Bean(name = "adsTransactionManager")
    public DataSourceTransactionManager adsTransactionManager(@Qualifier("adsDataSource") DataSource adsDataSource) {
        return new DataSourceTransactionManager(adsDataSource);
    }

    @Bean(name = "adsSqlSessionFactory")
    public SqlSessionFactory adsSqlSessionFactory(@Qualifier("adsDataSource") DataSource adsDataSource) throws
            Exception {
        final SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(adsDataSource);
        bean.setTypeAliasesPackage("com.zhongqi.model");

        //分页插件
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("returnPageInfo", "check");
        properties.setProperty("params", "count=countSql");
        pageHelper.setProperties(properties);

        //添加插件
        bean.setPlugins(new Interceptor[]{pageHelper});

        //添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            bean.setMapperLocations(resolver.getResources("classpath:business-mapper/*.xml"));
            return bean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
//        return bean.getObject();
    }

    @Bean(name = "adsSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("adsSqlSessionFactory") SqlSessionFactory
                                                             sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}