package br.com.washii.api.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.domain.Persistable;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
// Necessário implementar Persistable<UUID> pois o UUID será gerado externamente
// Para o jpa não achar que estamos fazendo uma operacão de update
public class Usuario implements Persistable<UUID> {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(name = "foto_perfil")
    private byte[] fotoPerfil;

    @Column(nullable = false)
    private boolean ativo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false)

    private TipoUsuario tipoUsuario;

    @Column(nullable = false, length = 100)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String estado;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<UsuarioTelefone> telefones = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Transient
    private boolean novo = true;

    @Override
    public boolean isNew() {
        return novo;
    }

    public String getTelefone() {
        if (telefones == null || telefones.isEmpty()) {
            return null;
        }
        Telefone tel = telefones.get(0).getTelefone();
        if (tel == null) {
            return null;
        }
        return tel.getDdd() + tel.getNumero();
    }
}
