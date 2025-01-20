package com.example.project_spring.security;

import com.example.project_spring.security.jwt.JwtTokenAuthenticationFilter;
import com.example.project_spring.security.jwt.JwtTokenProvider;
import com.example.project_spring.service.impl.CustomUserDetailsService;
import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableWebSecurity
public class WebSecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    public WebSecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder bCryptPasswordEncoder, CustomUserDetailsService userDetailService) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        // Konfigurisanje UserDetailsService i PasswordEncoder bez `and()`
        authenticationManagerBuilder.userDetailsService(userDetailService).passwordEncoder(bCryptPasswordEncoder);

        // Kreiranje AuthenticationManager-a
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Isključivanje CSRF
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .cors(cors -> {}) // Omogućavanje CORS-a
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/", "/api/users/login").permitAll()
                                .requestMatchers("/", "/api/users/register").permitAll()
                                .requestMatchers("/api/users/editProfile").authenticated()
                                .requestMatchers("/api/users/profile").authenticated()
                                .requestMatchers("/api/users/verify-email").permitAll()
                                .requestMatchers("/api/categories/allCategories").permitAll()
                                .requestMatchers("/api/users/search").authenticated()
                                .requestMatchers("/api/requests/received/").authenticated()
                                .requestMatchers("/api/requests/usersFriends/").authenticated()
                                .requestMatchers("/api/movie/getAllMovies").permitAll()
                                .requestMatchers("/uploads/**").permitAll()
                                .requestMatchers("api/favorite-movies/add-movie/").permitAll()
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtTokenAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/api/users/login", "/api/users/register");
    }

    @Bean(name = "webCorsConfigurer") // Dodeljen novi naziv beanu da izbegnemo konflikt
    public WebMvcConfigurer webCorsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Dozvoljava sve rute
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Dozvoljava navedene metode
                        .allowedHeaders("*") // Dozvoljava sve zaglavlja
                        .allowCredentials(true); // Dozvoljava slanje kolačića
            }
        };
    }

    @Configuration
    public class MultipartConfig {
        @Bean
        public MultipartResolver multipartResolver() {
            return new StandardServletMultipartResolver();
        }
    }

    // Dodatak za omogućavanje statičkih resursa
    @Configuration
    public class StaticResourceConfig implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            // Omogućava pristup fajlovima unutar 'uploads' direktorijuma
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations("file:uploads/")
                    .setCachePeriod(3600); // Opcionalno, keširanje fajlova na sat vremena
        }
    }
}
