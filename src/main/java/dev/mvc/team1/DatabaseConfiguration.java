package dev.mvc.team1;

import javax.sql.DataSource;

import org.mybatis.spring.annotation.MapperScan;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@PropertySource("classpath:/application.properties")  // 설정 파일 위치
@MapperScan(basePackages= {"dev.mvc.ai_consult",
                                        "dev.mvc.consult_match",
                                        "dev.mvc.hospital",
                                        "dev.mvc.hospital_species",
                                        "dev.mvc.memory",
                                        "dev.mvc.pet",
                                        "dev.mvc.alarm_log",
                                        "dev.mvc.post",
                                        "dev.mvc.postgood",
                                        "dev.mvc.recommendation",
                                        "dev.mvc.reply",
                                        "dev.mvc.animal_today",
                                        "dev.mvc.replylike",
                                        "dev.mvc.species",
                                        "dev.mvc.news",
                                        "dev.mvc.sms_log",
                                        "dev.mvc.users",
                                        "dev.mvc.notice",
                                        "dev.mvc.calendar",
                                        "dev.mvc.feedback"}) // DAO interface를 찾는 위치
public class DatabaseConfiguration {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Bean
    @ConfigurationProperties(prefix="spring.datasource.hikari")  // 설정 파일의 접두사 선언 spring.datasource.hikari....
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }
    
    @Bean
    public DataSource dataSource() throws Exception{
        DataSource dataSource = new HikariDataSource(hikariConfig());
        System.out.println(dataSource.toString());  // 정상적으로 연결 되었는지 해시코드로 확인
        return dataSource;
    }
    
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception{
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        // "/src/main/resources/mybatis" 폴더의 파일명이 "xml"로 끝나는 파일 매핑
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:/mybatis/**/*.xml"));
        
        return sqlSessionFactoryBean.getObject();
    }
    
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory){
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}