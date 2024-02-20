package com.example.gateway.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
public class SecurityConfig {

    private AuthenticationManager authenticationManager;

    private SecurityContextRepository securityContextRepository;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable);

        http
                .exceptionHandling(exceptionHandlingSpec ->
                        exceptionHandlingSpec.authenticationEntryPoint((swe, e) ->
                                Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))
                        ).accessDeniedHandler((swe, e) ->
                                Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN))
                        ))
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange(authorizeExchangeSpec ->
                        authorizeExchangeSpec
                                .pathMatchers(HttpMethod.POST, "/api/category/**").hasRole("ADMIN")
                                .pathMatchers(HttpMethod.PUT, "/api/category/**").hasRole("ADMIN")
                                .pathMatchers(HttpMethod.DELETE, "/api/category/**").hasRole("ADMIN")
                                .pathMatchers(HttpMethod.GET, "/api/category/**").authenticated()

                                .pathMatchers(HttpMethod.POST, "api/product/{productId}/quantity").authenticated()
                                .pathMatchers(HttpMethod.POST, "/api/product/**").hasRole("ADMIN")
                                .pathMatchers(HttpMethod.PUT, "/api/product/**").hasRole("ADMIN")
                                .pathMatchers(HttpMethod.DELETE, "/api/product/**").hasRole("ADMIN")
                                .pathMatchers(HttpMethod.GET, "/api/product/**").authenticated()


                                .pathMatchers(HttpMethod.DELETE, "/api/bill/**").hasRole("ADMIN")
                                .pathMatchers(HttpMethod.POST, "/api/bill/**").authenticated()

                                .pathMatchers(HttpMethod.GET, "/admin").hasRole("ADMIN")

                                .pathMatchers("/api/user/**").permitAll()
                                .pathMatchers("/**").permitAll()
                                .anyExchange().authenticated());

        return http.build();
    }
}