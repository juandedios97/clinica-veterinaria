package com.clinica.veterinaria.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.clinica.veterinaria.service.UsuarioService;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/assets/**", "/images/**", "/error").permitAll()
                .requestMatchers("/api/search/**", "/public/**").permitAll()
                .requestMatchers("/login").permitAll()
                .requestMatchers("/cliente/**").hasRole("CLIENTE")
                .requestMatchers("/usuarios/**").hasRole("ADMIN")
                .requestMatchers("/historial/**").hasAnyRole("ADMIN", "VETERINARIO")
                .requestMatchers("/citas/**").hasAnyRole("ADMIN", "RECEPCIONISTA")
                .requestMatchers("/facturacion/**").hasAnyRole("ADMIN", "RECEPCIONISTA")
                .requestMatchers("/mascotas/**", "/propietarios/**", "/veterinarios/**", "/vacunacion/**").hasRole("ADMIN")
                .requestMatchers("/").permitAll()
                .requestMatchers("/dashboard").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA")
                .anyRequest().authenticated())
            .formLogin(login -> login
                .loginPage("/login")
                .defaultSuccessUrl("/post-login", true)
                .permitAll())
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll());

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(usuarioService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
