package org.thelidia.demo;

import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.tracing.TracingInstrumentation;
import org.jooq.SQLDialect;
import org.jooq.impl.CallbackExecuteListener;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public DefaultDSLContext dsl(DefaultConfiguration configuration) {
        return new DefaultDSLContext(configuration);
    }

    @Bean
    public DefaultConfiguration configuration(DataSourceConnectionProvider connectionProvider, LatencySimulator latencySimulator) {
        DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
        jooqConfiguration.set(SQLDialect.POSTGRES);
        jooqConfiguration.set(connectionProvider);
        jooqConfiguration.set(latencySimulator);
//        jooqConfiguration
//                .set(new DefaultExecuteListenerProvider(new LoggerListener()));
        return jooqConfiguration;
    }

    @Bean
    public DataSourceConnectionProvider connectionProvider(DataSource dataSource) {
        return new DataSourceConnectionProvider
                (new TransactionAwareDataSourceProxy(dataSource));
    }

//    @Bean
//    Instrumentation instrumentation() {
//        return new TracingInstrumentation(
//                TracingInstrumentation.Options
//                        .newOptions()
//                        .includeTrivialDataFetchers(false)
//        );
//    }
}
