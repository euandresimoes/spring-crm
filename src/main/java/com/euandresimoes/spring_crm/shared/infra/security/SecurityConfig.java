package com.euandresimoes.spring_crm.shared.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.euandresimoes.spring_crm.auth.domain.UserRoles;

@Configuration
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtFilter;

        public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
                this.jwtFilter = jwtFilter;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http.csrf(csrf -> csrf
                                .ignoringRequestMatchers("/h2-console/**")
                                .disable())

                                .headers(headers -> headers
                                                .frameOptions(frame -> frame.sameOrigin()))

                                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth
                                                // Swagger
                                                .requestMatchers(
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html")
                                                .permitAll()

                                                // H2 Database
                                                .requestMatchers(
                                                                "/h2-console/**")
                                                .permitAll()

                                                // Auth
                                                .requestMatchers("api/v1/auth/**").permitAll()

                                                // Admin
                                                .requestMatchers("api/v1/admin/**")
                                                .hasRole(UserRoles.ADMIN.getRole())

                                                // Outro
                                                .anyRequest().authenticated());

                http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

}
