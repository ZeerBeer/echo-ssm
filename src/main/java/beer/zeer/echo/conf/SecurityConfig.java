package beer.zeer.echo.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    // table name custom
    String userTable = "user_t";
    String authTable = "auth_t";
    // column name custom
    String usernameColumn = "username";
    String passwordColumn = "u_password";
    String authorityColumn = "auth";

    @Bean public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired DataSource dataSource;
    @Autowired BCryptPasswordEncoder passwordEncoder;

    @Override public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers("/ping/**");
    }

    @Override protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource)
                .usersByUsernameQuery(String.format("SELECT %s,%s,1 as PLACEHOLDER FROM %s WHERE username = ?",
                        usernameColumn, passwordColumn, userTable))
                .authoritiesByUsernameQuery(String.format("SELECT %s,%s FROM %s WHERE username = ?",
                        usernameColumn, authorityColumn, authTable))
                .passwordEncoder(passwordEncoder);
    }
}
