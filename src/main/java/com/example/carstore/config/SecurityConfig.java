package com.example.carstore.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.carstore.service.AccountUserDetailsService;
import com.example.carstore.service.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomOAuth2UserService oAuth2UserService;

    @Autowired
    private AccountUserDetailsService accountUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf().disable()
            // Cấu hình phân quyền
            .authorizeRequests()
                .antMatchers("/", "/login/**", "/signup/**", "/css/**", "/js/**", "/oauth2/**").permitAll()
                .antMatchers("/car/list", "/car/detail/**").permitAll()
                .antMatchers("/car/create", "/car/save", "/car/edit/**", "/car/delete/**").hasRole("ADMIN")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/api/cars/**", "/api/admin/**").hasRole("ADMIN")
                .antMatchers("/api/cart/**", "/api/orders/**", "/api/profile/**").authenticated()
                .anyRequest().permitAll()
            .and()
            // Cấu hình Login bằng Form (Username/Password)
            .formLogin()
                .loginPage("/login/form")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            .and()
            // Cấu hình Login bằng OAuth2 (Google/Facebook)
            .oauth2Login()
                .loginPage("/login/form")
                .userInfoEndpoint()
                    .userService(oAuth2UserService)
                .and()
                .defaultSuccessUrl("/", true)
            .and()
            // Cấu hình Logout
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(accountUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}