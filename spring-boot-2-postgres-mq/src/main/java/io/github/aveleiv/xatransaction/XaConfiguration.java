package io.github.aveleiv.xatransaction;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.spring.AtomikosConnectionFactoryBean;
import com.atomikos.spring.AtomikosDataSourceBean;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.jms.ConnectionFactory;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import java.util.Properties;

@Configuration
@EnableConfigurationProperties({ActiveMQConnectionDetails.class, JdbcConnectionDetails.class})
public class XaConfiguration {

    @Bean(initMethod = "init", destroyMethod = "close")
    public UserTransactionManager atomikosTransactionManager() {
        UserTransactionManager utm = new UserTransactionManager();
        utm.setForceShutdown(false);
        return utm;
    }

    @Bean
    @Primary
    public UserTransaction atomikosUserTransaction(UserTransactionManager atomikosTransactionManager) {
        return atomikosTransactionManager;
    }

    @Bean("xaTransactionManager")
    public PlatformTransactionManager transactionManager(UserTransaction userTransaction, TransactionManager transactionManager) {
        return new JtaTransactionManager(userTransaction, transactionManager);
    }

    @Bean
    @DependsOn("atomikosTransactionManager")
    public ConnectionFactory connectionFactory(ActiveMQConnectionDetails connectionDetails) {
        var factory = new AtomikosConnectionFactoryBean();
        factory.setUniqueResourceName("activemq");
        factory.setMaxPoolSize(10);

        ActiveMQXAConnectionFactory activeMQXAConnectionFactory = new ActiveMQXAConnectionFactory();
        activeMQXAConnectionFactory.setBrokerURL(connectionDetails.brokerUrl());

        factory.setXaConnectionFactory(activeMQXAConnectionFactory);
        return factory;
    }

    @Bean
    @Primary
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory);
        jmsTemplate.setSessionTransacted(true);
        return jmsTemplate;
    }


    @Bean
    @DependsOn("atomikosTransactionManager")
    AtomikosDataSourceBean dataSource(JdbcConnectionDetails properties) {
        var ds = new AtomikosDataSourceBean();
        ds.setUniqueResourceName("postgres");
        ds.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");
        ds.setMaxPoolSize(10);


        var props = new Properties();
        props.setProperty("user", properties.username());
        props.setProperty("password", properties.password());
        props.setProperty("url", properties.url());
        ds.setXaProperties(props);
        return ds;
    }
}
