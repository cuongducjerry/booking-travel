package vn.travel.booking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // allow which URLs can connect to the backend
        configuration.setAllowedOrigins(
                Arrays.asList("http://localhost:3000",
                        "http://localhost:4173",
                        "http://localhost:5173",
                        "http://localhost"
                ));

        // which methods are connected
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // header sections that are allowed to be uploaded
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "x-no-retry", "delay"));

        // do you want cookies included
        configuration.setAllowCredentials(true);

        // pre-flight request caching time (in seconds)
        configuration.setMaxAge(3600L);
        // How long the response from a pre-flight request can be cached by clients

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply this configuration to all paths
        return source;
    }

}
