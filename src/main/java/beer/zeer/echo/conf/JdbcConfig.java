package beer.zeer.echo.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

@Configuration
@EnableJdbcRepositories("beer.zeer.echo")
public class JdbcConfig extends AbstractJdbcConfiguration {
    // database config file
    InputStream in = JdbcConfig.class.getClassLoader().getResourceAsStream("database-jdbc.properties");

    @Bean public DataSource dataSource() {
        try{
            Properties properties = new Properties();
            properties.load(in);
            DriverManagerDataSource dataSource = new DriverManagerDataSource(properties.getProperty("url"),
                    properties.getProperty("username"), properties.getProperty("password"));
            dataSource.setDriverClassName(properties.getProperty("driver-class-name"));
            logger.log(LoggerConfig.DEBUG, String.format("[load data source ok] [url:%s u:%s p:%s d:%s]", dataSource.getUrl(),
                    dataSource.getUsername(), dataSource.getPassword(), properties.getProperty("driver-class-name")));
            return dataSource;
        }catch (IOException e){
            logger.log(LoggerConfig.DEBUG, String.format("[load data source failed] [e:%s]", e.toString()));
            return null;
        }
    }

    @Bean NamedParameterJdbcOperations namedParameterJdbcOperations(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean TransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    Logger logger = Logger.getLogger(this.getClass().getName());
}
