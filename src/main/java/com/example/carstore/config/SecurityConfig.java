package com.example.carstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.carstore.service.AccountUserDetailsService;

@Configuration
public class SecurityConfig {

  @Bean
public SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider authenticationProvider) throws Exception {

    http
        .csrf().disable()
        .authenticationProvider(authenticationProvider)

        .authorizeRequests()
            .antMatchers("/", "/login/**", "/signup/**", "/css/**", "/js/**", "/oauth2/**").permitAll()
            // MVC pages for SEO: car list and detail
            .antMatchers("/car/list", "/car/detail/**").permitAll()
            // Admin MVC
            .antMatchers("/car/create", "/car/save", "/car/edit/**", "/car/delete/**").hasRole("ADMIN")
            .antMatchers("/admin/**").hasRole("ADMIN")
            // REST APIs
            .antMatchers("/api/cars/**", "/api/admin/**").hasRole("ADMIN")
            .antMatchers("/api/cart/**", "/api/orders/**", "/api/profile/**").authenticated()
            .anyRequest().permitAll()
        .and()

        .formLogin()
            .loginPage("/login/form")
            .loginProcessingUrl("/login")
            .defaultSuccessUrl("/", true)
            .permitAll()
        .and()

        .logout()
            .logoutUrl("/logout")
            .logoutSuccessUrl("/");

    return http.build();
}
    @Bean
    public UserDetailsService users(AccountUserDetailsService accountUserDetailsService) {
        return accountUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(AccountUserDetailsService accountUserDetailsService,
                                                            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(accountUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}