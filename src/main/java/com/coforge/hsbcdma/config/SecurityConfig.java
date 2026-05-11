package com.coforge.hsbcdma.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * This is Security Configuration class which handles user permissions and role based access
 * @author Vandana Pal
 */

//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
//public class SecurityConfig {
//
//    @Autowired
//    private JwtAuthenticationFilter jwtFilter;
//
//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
//
//    /*@Bean
//    public PasswordEncoder passwordEncoder(){
//        return new IteratedSha256PasswordEncoder(150_000);
//    }*/
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
////       return http
////                .csrf(csrf -> csrf.disable())
////               .cors(cors->{})
////                .authorizeHttpRequests(auth -> auth
////                        //.requestMatchers("/addNewDemand/**").permitAll()
////				.requestMatchers("/addNewDemand/**").authenticated()
////                        .requestMatchers("/talent_dashboard/**").authenticated()
////                        .requestMatchers("/profile_tracker/**").authenticated()
////                        .requestMatchers("/auth_user/**").permitAll()
////                        .requestMatchers("/auth_user/login", "/auth_user/forgot-password", "/auth_user/change-password").permitAll()
////                        .requestMatchers("/auth_user/register").authenticated()
////                        .requestMatchers("/api/**").authenticated() //add roleBased permissions here
////                        .requestMatchers("/user_management/**").authenticated()
////                        .requestMatchers("/dummy/**").permitAll()
////                        .anyRequest().authenticated()
////        )
////               .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
////                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
////                .build();
//
//        return http
//                // ✅ REST API → CSRF disabled
//                .csrf(csrf -> csrf.disable())
//
//                // ✅ ENABLE CORS PROPERLY
//                .cors(Customizer.withDefaults())
//
//                // ✅ Stateless because JWT
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//
//                .authorizeHttpRequests(auth -> auth
//
//                        // ✅ CRITICAL: allow all OPTIONS requests (CORS preflight)
//                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//
//                        // ✅ Public auth endpoints
//                        .requestMatchers(
//                                "/auth_user/login",
//                                "/auth_user/forgot-password",
//                                "/auth_user/change-password"
//                        ).permitAll()
//
//                        // ✅ Auth APIs base
//                        .requestMatchers("/auth_user/**").permitAll()
//
//                        // ✅ Other APIs
//                        .requestMatchers("/addNewDemand/**").authenticated()
//                        .requestMatchers("/talent_dashboard/**").authenticated()
//                        .requestMatchers("/profile_tracker/**").authenticated()
//                        .requestMatchers("/api/**").authenticated()
//                        .requestMatchers("/profiles/**").authenticated()
//                        .requestMatchers("/user_management/**").authenticated()
//
//                        // ✅ Open endpoints
//                        .requestMatchers("/dummy/**").permitAll()
//
//                        // ✅ Everything else secured
//                        .anyRequest().authenticated()
//                )
//
//                // ✅ IMPORTANT: JWT filter AFTER OPTIONS is allowed
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
//
//                .build();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//}

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // ✅ Disable CSRF for REST APIs
                .csrf(csrf -> csrf.disable())

                // ✅ Enable CORS (uses CorsConfigurationSource bean)
                .cors(Customizer.withDefaults())

                // ✅ JWT → Stateless
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // ✅ Authorization rules
                .authorizeHttpRequests(auth -> auth

                        // ✅ CRITICAL: Always allow OPTIONS (CORS preflight)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ✅ Auth APIs
                        .requestMatchers(
                                "/auth_user/login",
                                "/auth_user/forgot-password",
                                "/auth_user/change-password"
                        ).permitAll()

                        .requestMatchers("/auth_user/**").permitAll()

                        // ✅ Secured business APIs
                        .requestMatchers("/addNewDemand/**").authenticated()
                        .requestMatchers("/talent_dashboard/**").authenticated()
                        .requestMatchers("/profile_tracker/**").authenticated()
                        .requestMatchers("/profiles/**").authenticated()
                        .requestMatchers("/user_management/**").authenticated()
                        .requestMatchers("/api/**").authenticated()

                        // ✅ Open endpoints
                        .requestMatchers("/dummy/**").permitAll()
                        .requestMatchers("/auth_user/refresh").permitAll()

                        // ✅ Everything else secure
                        .anyRequest().authenticated()
                );

        // ✅ IMPORTANT: JWT filter AFTER CORS + OPTIONS handling
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}
