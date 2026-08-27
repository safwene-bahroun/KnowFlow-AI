package tn.knowflowai.backend.Security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        // =====================================================
        // ANGULAR
        // =====================================================

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:4200"
                )
        );

        // =====================================================
        // HTTP METHODS
        // =====================================================

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );

        // =====================================================
        // HEADERS
        // =====================================================

        configuration.setAllowedHeaders(
                List.of("*")
        );

        // =====================================================
        // JWT / CREDENTIALS
        // =====================================================

        configuration.setAllowCredentials(true);

        // =====================================================
        // APPLY TO ALL ENDPOINTS
        // =====================================================

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}