package com.coforge.hsbcdma.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CommonConfig {

    //Below method is to disable security i.e. to deactivate lgoin page
   /* @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Allow *everything*
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                // Disable CSRF if you're doing state-less APIs or testing
                .csrf(csrf -> csrf.disable())
                // Disable form login
                .formLogin(form -> form.disable())
                // Disable HTTP Basic
                .httpBasic(Customizer.withDefaults()); // or .httpBasic(http -> http.disable())

        return http.build();
    }*/

    @Bean
    WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("GET","POST","PUT","DELETE","PATCH","OPTIONS");
            }
        };
    }

}
