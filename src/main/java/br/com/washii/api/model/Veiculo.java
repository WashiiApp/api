package br.com.washii.api.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "veiculo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
// Filtra automaticamente os veículos que não estão ativos
@SQLRestriction("ativo = true")
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    /*
     * O parâmetro 'name' na anotação @JoinColumn refere-se ao nome da coluna
     * física nesta tabela (veiculo). Embora a chave primária na tabela
     * cliente se chame 'id_usuario', a FK aqui foi nomeada como 'id_cliente'.
     */
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria_veiculo")
    private CategoriaVeiculo categoriaVeiculo;

    @Column(nullable = false, length = 30)
    private String cor;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(nullable = false, unique = true, length = 8)
    private String placa;

    @Column(nullable = false, length = 50)
    private String marca;

    @Column(nullable = false, length = 100)
    private String modelo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}