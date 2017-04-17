package org.xdi.oxd.badgemanager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.xdi.oxd.badgemanager.security.AuthoritiesConstants;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.authorizeRequests().antMatchers("/").permitAll().antMatchers("/error").permitAll()
                .antMatchers("/home").permitAll().antMatchers("/user").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/logout").hasAnyAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/img").permitAll()
                .antMatchers("/organization").permitAll();


    }


}
