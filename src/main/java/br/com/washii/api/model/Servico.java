package br.com.washii.api.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "servico",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id_lavajato", "nome"}) // Impede nomes duplicados no mesmo lava-jato
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
// Filtra automaticamente os servicos com a flag abaixo
@SQLRestriction("ativo = true")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lavajato")
    private LavaJato lavaJato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria_servico")
    private CategoriaServico categoriaServico;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, columnDefinition = "text") // Especifica que é do tipo text (sem limite padrão do varchar)
    private String descricao;

    @Column(nullable = false)
    private boolean ativo = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}