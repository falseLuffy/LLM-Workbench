package com.example.llm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/sys/health").permitAll()
            .antMatchers("/v1/proxy/data/**").permitAll()
            .antMatchers("/v1/proxy/knowledge/**").permitAll()
            .antMatchers("/v1/proxy/stats/**").permitAll()
            .antMatchers("/v1/agent/**").permitAll()
            .antMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**").permitAll()
            .anyRequest().authenticated();
            
        // Note: In a real project, we would add the JWT filter here
    }
}
