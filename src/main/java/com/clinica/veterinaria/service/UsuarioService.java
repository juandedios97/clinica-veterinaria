package com.clinica.veterinaria.service;

import com.clinica.veterinaria.model.RolUsuario;
import com.clinica.veterinaria.model.Usuario;
import com.clinica.veterinaria.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        seedDefaultAdmin();
    }

    public List<Usuario> findAll() {
        return repo.findAll();
    }

    public Usuario findById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + id));
    }

    public Usuario findByUsername(String username) {
        return repo.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + username));
    }

    public Usuario save(Usuario usuario) {
        Usuario existente = usuario.getId() != null ? findById(usuario.getId()) : null;

        if (existente == null) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        } else {
            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                usuario.setPassword(existente.getPassword());
            } else {
                usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            }
        }

        if (usuario.getRol() != RolUsuario.CLIENTE) {
            usuario.setPropietario(null);
        }

        return repo.save(usuario);
    }

    public void deleteById(Long id) {
        Usuario usuario = findById(id);
        if ("admin".equalsIgnoreCase(usuario.getUsername())) {
            throw new IllegalStateException("No se puede eliminar el usuario administrador principal");
        }
        repo.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = repo.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return User.builder()
            .username(usuario.getUsername())
            .password(usuario.getPassword())
            .disabled(!usuario.isActivo())
            .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())))
            .build();
    }

    private void seedDefaultAdmin() {
        if (repo.existsByUsername("admin")) {
            return;
        }
        Usuario admin = new Usuario();
        admin.setNombreCompleto("Administrador Principal");
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRol(RolUsuario.ADMIN);
        admin.setActivo(true);
        repo.save(admin);
    }
}
