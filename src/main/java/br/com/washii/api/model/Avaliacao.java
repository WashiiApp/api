package br.com.washii.api.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "avaliacao",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id_cliente", "id_servico"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_servico")
    private Servico servico;

    @Column(columnDefinition = "text")
    private String comentario;

    @Column
    private LocalDate data;

    /*
     * Nota: A restrição "check (nivel_satisfacao between 1 and 5)" sera garantida
     * pelo banco de dados conforme o script SQL de schema.sql, mas a nivel de codigo, talvez,
     * pode usar @Min(1) e @Max(5) do pacote 'jakarta.validation.constraints'. (A se pensar/Implementar )
     * TODO
     */
    @Column(name = "nivel_satisfacao")
    private Integer nivelSatisfacao;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
