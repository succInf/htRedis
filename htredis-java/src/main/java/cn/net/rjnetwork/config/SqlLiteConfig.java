package cn.net.rjnetwork.config;

import cn.net.rjnetwork.before.BeforeApp;
import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;

@EnableTransactionManagement
@Component
@Slf4j
@MapperScan(basePackages = {"cn.net.rjnetwork.mapper"}, sqlSessionFactoryRef = "mainSqlSessionFactory")
public class SqlLiteConfig {

    @Resource
    @Lazy
    MybatisPlusInterceptor mybatisPlusInterceptor;

    private static final String driverClass = "org.sqlite.JDBC";
    private static final Integer initialSize=100;
    private static final Integer minIdle = 100;


    private static final  Integer maxActive = 2000;


    private static final  Integer maxWait = 60000;

    private static final Integer runsMillis = 60000;


    private static final Integer minEvictableIdleTimeMillis = 300000;


    private static final  Integer maxEvictableIdleTimeMillis = 900000;

    private static final String validationQuery = "SELECT 1";

    private static final  Boolean testWhileIdle = true;

    private static final   Boolean testOnBorrow = true;

    private static final  Boolean testOnReturn = false;

    private static final  Boolean poolPreparedStatements = true;
    private static final  Integer maxpoolPreparedStatementperConnectionSize = 2000;

    private  static final  Boolean defaultAutoCommit = true;


    @Bean("mainDataSource")
    @Primary
    @ConditionalOnMissingBean
    public DataSource druidDataSource() {
        log.info("加载EQ数据源----start----");
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        String dbPath = BeforeApp.getDbPath();
        log.info("dbPath:{}", dbPath);
        //url地址重新加载获取。
        dataSource.setUrl("jdbc:sqlite:file:" + dbPath);
//        dataSource.setUsername(userName);
//        dataSource.setPassword(password);
        dataSource.setInitialSize(initialSize);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxActive(maxActive);
        dataSource.setMaxWait(maxWait);
        dataSource.setTimeBetweenEvictionRunsMillis(runsMillis);
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        dataSource.setMaxEvictableIdleTimeMillis(maxEvictableIdleTimeMillis);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestWhileIdle(testWhileIdle);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setTestOnReturn(testOnReturn);
        dataSource.setPoolPreparedStatements(poolPreparedStatements);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(maxpoolPreparedStatementperConnectionSize);
        dataSource.setDefaultAutoCommit(defaultAutoCommit);
        try {
            dataSource.init();
        } catch (SQLException e) {
            log.error("初始化数据源报错{}",e.getMessage(), e);
        }
        log.info("加载EQ数据源----end----");
        return dataSource;
    }


    @Bean(name = "mainSqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("mainDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean sessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:/mapper/**/*.xml"));
        Interceptor[] plugins = {mybatisPlusInterceptor};
        sessionFactoryBean.setPlugins(plugins);
        return sessionFactoryBean.getObject();
    }

    /**
     * 分页插件
     */
    @Bean("mybatisPlusInterceptorBean")
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //乐观锁
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        //分页锁
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
