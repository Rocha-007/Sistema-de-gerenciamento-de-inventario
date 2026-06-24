package com.projeto.inventario.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados para registro de um novo usuario.")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador gerado pelo banco.", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Nome unico do usuario.", example = "maria", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Column(nullable = false)
    @Schema(description = "Senha que sera armazenada de forma criptografada.", example = "Senha@123", format = "password", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Column(nullable = false)
    @Schema(description = "E-mail unico do usuario.", example = "maria@email.com", format = "email", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Column(name = "ativo")
    @Builder.Default
    @Schema(description = "Situacao da conta.", accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean ativo = true;

    // Implementação de UserDetails
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public static UsuarioBuilder builder() {
        return new UsuarioBuilder();
    }

    public static class UsuarioBuilder {
        private Long id;
        private String username;
        private String password;
        private String email;
        private Boolean ativo = true;

        public UsuarioBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UsuarioBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UsuarioBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UsuarioBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UsuarioBuilder ativo(Boolean ativo) {
            this.ativo = ativo;
            return this;
        }

        public Usuario build() {
            Usuario usuario = new Usuario();
            usuario.setId(id);
            usuario.setUsername(username);
            usuario.setPassword(password);
            usuario.setEmail(email);
            usuario.setAtivo(ativo);
            return usuario;
        }
    }

    @Override
    @Schema(hidden = true)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    @Schema(hidden = true)
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Schema(hidden = true)
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @Schema(hidden = true)
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Schema(hidden = true)
    public boolean isEnabled() {
        return this.ativo;
    }
}
