package com.project.shopapp.config;

import com.project.shopapp.filter.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@EnableGlobalAuthentication
public class WebSecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request -> {
                    request
                            .requestMatchers(
                                    String.format("%s/users/register",apiPrefix),
                                    String.format("%s/users/login",apiPrefix)
                            ).permitAll()
                            .requestMatchers(HttpMethod.GET,
                                    String.format("%s/roles/**", apiPrefix)).permitAll()
                            .requestMatchers(HttpMethod.GET,
                                    String.format("%s/categories/**",apiPrefix)).permitAll()
                            .requestMatchers(HttpMethod.POST,
                                    String.format("%s/categories/**",apiPrefix)).hasAnyRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT,
                                    String.format("%s/categories/**",apiPrefix)).hasAnyRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE,
                                    String.format("%s/categories/**",apiPrefix)).hasAnyRole("ADMIN")
                            .requestMatchers(HttpMethod.GET,
                                    String.format("%s/products/images/**",apiPrefix)).permitAll()
                            .requestMatchers(HttpMethod.GET,
                                    String.format("%s/products/**",apiPrefix)).permitAll()
                            .requestMatchers(HttpMethod.POST,
                                    String.format("%s/products/**",apiPrefix)).hasAnyRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT,
                                    String.format("%s/products/**",apiPrefix)).hasAnyRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE,
                                    String.format("%s/products/**",apiPrefix)).hasAnyRole("ADMIN")
                            .requestMatchers(HttpMethod.POST,
                                    String.format("%s/orders/**",apiPrefix)).hasAnyRole("USER")
                            .requestMatchers(HttpMethod.GET,
                                    String.format("%s/orders/**",apiPrefix)).permitAll()
                            .requestMatchers(HttpMethod.PUT,
                                    String.format("%s/orders/**",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE,
                                    String.format("%s/orders/**",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(HttpMethod.POST,
                                    String.format("%s/order_detail/**",apiPrefix)).hasAnyRole("USER")
                            .requestMatchers(HttpMethod.GET,
                                    String.format("%s/order_detail/**",apiPrefix)).permitAll()
                            .requestMatchers(HttpMethod.PUT,
                                    String.format("%s/order_detail/**",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE,
                                    String.format("%s/order_detail/**",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(HttpMethod.GET,
                                    String.format("%s/carts/**",apiPrefix)).permitAll()
                            .requestMatchers(HttpMethod.POST,
                                    String.format("%s/carts/**",apiPrefix)).permitAll()
                            .requestMatchers(HttpMethod.PUT,
                                    String.format("%s/carts/**",apiPrefix)).permitAll()
                            .requestMatchers(HttpMethod.DELETE,
                                    String.format("%s/carts/**",apiPrefix)).permitAll()
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                            .anyRequest().authenticated();
                });

        return http.build();
    }

}
