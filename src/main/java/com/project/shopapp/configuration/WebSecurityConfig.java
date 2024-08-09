package com.project.shopapp.configuration;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import com.project.shopapp.filters.JwtTokenFilter;
import com.project.shopapp.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    @Value("${api.prefix}")
    private String apiPrefix;
    private final JwtTokenFilter jwtTokenFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(request -> {
                request.requestMatchers(
                        String.format("%s/users/register", apiPrefix),
                        String.format("%s/users/login", apiPrefix)
                    ).permitAll()
                    .requestMatchers(GET, String.format("%s/roles**", apiPrefix)).permitAll()
                    .requestMatchers(GET, String.format("%s/categories**", apiPrefix)).permitAll()
                    .requestMatchers(GET, String.format("%s/categories/**", apiPrefix)).permitAll()
                    .requestMatchers(POST, String.format("%s/categories/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                    .requestMatchers(PUT, String.format("%s/categories/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                    .requestMatchers(DELETE, String.format("%s/categories/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                    .requestMatchers(GET, String.format("%s/products**", apiPrefix)).permitAll()
                    .requestMatchers(GET, String.format("%s/products/**", apiPrefix)).permitAll()
                    .requestMatchers(GET, String.format("%s/products/images/*", apiPrefix)).permitAll()
                    .requestMatchers(POST, String.format("%s/products**", apiPrefix)).hasAnyRole(Role.ADMIN)
                    .requestMatchers(PUT, String.format("%s/products/**", apiPrefix)).hasAnyRole(Role.ADMIN)

                    //let login as ROLE_USER for testing
                    /*
                    //If you comment this, you must add @PreAuthorize("hasRole('ROLE_ADMIN')")
                    .requestMatchers(DELETE,
                            String.format("%s/products/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                    */
                    .requestMatchers(POST, String.format("%s/orders/**", apiPrefix)).hasAnyRole(Role.USER)
                    .requestMatchers(GET, String.format("%s/orders/**", apiPrefix)).permitAll()
                    .requestMatchers(PUT, String.format("%s/orders/**", apiPrefix)).hasRole(Role.ADMIN)
                    .requestMatchers(DELETE, String.format("%s/orders/**", apiPrefix)).hasRole(Role.ADMIN)
                    .requestMatchers(POST, String.format("%s/order_details/**", apiPrefix)).hasAnyRole(Role.USER)
                    .requestMatchers(GET, String.format("%s/order_details/**", apiPrefix)).permitAll()
                    .requestMatchers(PUT, String.format("%s/order_details/**", apiPrefix)).hasRole(Role.ADMIN)
                    .requestMatchers(DELETE, String.format("%s/order_details/**", apiPrefix)).hasRole(Role.ADMIN)
                    .requestMatchers(GET, String.format("%s/healthcheck/**", apiPrefix))
                    .permitAll()
                    .anyRequest().authenticated();
            })
        ;
        return http.build();
    }
}