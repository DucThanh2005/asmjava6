package com.example.carstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.carstore.service.AccountUserDetailsService;
import com.example.carstore.service.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final CustomOAuth2UserService oAuth2UserService;
        private final AccountUserDetailsService accountUserDetailsService;

        public SecurityConfig(
                        CustomOAuth2UserService oAuth2UserService,
                        AccountUserDetailsService accountUserDetailsService) {

                this.oAuth2UserService = oAuth2UserService;
                this.accountUserDetailsService = accountUserDetailsService;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

                http
                                .csrf().disable()

                                .authorizeRequests()

                                // PUBLIC
                                .antMatchers(
                                                "/",
                                                "/login/**",
                                                "/signup/**",
                                                "/css/**",
                                                "/js/**",
                                                "/images/**",
                                                "/videos/**",
                                                "/oauth2/**")
                                .permitAll()

                                .antMatchers(
                                                "/api/auth/**")
                                .permitAll()

                                // CAR PAGE
                                .antMatchers(
                                                "/car/list",
                                                "/car/detail/**",
                                                "/car/inventory")
                                .permitAll()

                                // API XE
                                .antMatchers(HttpMethod.GET, "/api/cars/**")
                                .permitAll()

                                // GIỎ HÀNG VUE API
                                .antMatchers("/api/cart/**", "/cart/**")
                                .permitAll()

                                // ADMIN
                                .antMatchers(
                                                "/car/create",
                                                "/car/save",
                                                "/car/edit/**",
                                                "/car/delete/**",
                                                "/admin/**",
                                                "/api/admin/**",
                                                "/api/upload/**")
                                .hasRole("ADMIN")

                                .antMatchers("/api/cars/**")
                                .hasRole("ADMIN")

                                // USER API
                                .antMatchers(
                                                "/api/orders/**",
                                                "/api/profile/**")
                                .authenticated()

                                .anyRequest()
                                .permitAll()

                                .and()

                                .formLogin()
                                .loginPage("/login/form")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/", true)
                                .permitAll()

                                .and()

                                .oauth2Login()
                                .loginPage("/login/form")
                                .userInfoEndpoint()
                                .userService(oAuth2UserService)
                                .and()
                                .defaultSuccessUrl("/", true)

                                .and()

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