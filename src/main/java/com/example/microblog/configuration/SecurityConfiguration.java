package com.example.microblog.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

@EnableWebSecurity
@Configuration      // klasa o specyficznym znaczeiu implementująca globalne zabezpieczenia w aplikacji
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{
    DataSource dataSource;
    @Autowired
    public SecurityConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected void configure(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .authorizeRequests()
                    // żądania wymagające logowania
                    .antMatchers("/createPost").authenticated()
                    .antMatchers("/myWall").authenticated()
                    .antMatchers("/editUser").authenticated()
                    .anyRequest().permitAll()
                    .and().csrf().disable()
                .formLogin()
                    .loginPage("/login")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .loginProcessingUrl("/login_process")
                    .failureUrl("/login?error=true")
                    .defaultSuccessUrl("/myWall")
                    .and()
                .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login");
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .usersByUsernameQuery("SELECT u.login,u.passhash,u.status FROM user u WHERE u.login=?")
                .authoritiesByUsernameQuery(
                        "SELECT u.login, u.status FROM user u  WHERE u.login=?"
                )
                .dataSource(dataSource)
                .passwordEncoder(new BCryptPasswordEncoder());
    }
}
