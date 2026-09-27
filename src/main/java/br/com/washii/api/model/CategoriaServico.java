package br.com.washii.api.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "categoria_servico")
@Data
@NoArgsConstructor
@AllArgsConstructor
/*
 * @EqualsAndHashCode(onlyExplicitlyIncluded = true):
 * Por padrão, o Lombok usaria TODOS os campos da classe (id, nome, createdAt)
 * para gerar os métodos equals() e hashCode().
 * O 'onlyExplicitlyIncluded = true' desativa esse comportamento padrão.
 * Ele instrui o Lombok a usar APENAS os campos que estiverem explicitamente
 * marcados com a anotação @EqualsAndHashCode.Include.
 *
 * É uma excelente prática para entidades JPA, pois comparar entidades
 * por todos os campos pode causar problemas de performance ou loops
 * infinitos (em caso de relacionamentos bidirecionais).
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CategoriaServico {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    /*
     * @EqualsAndHashCode.Include:
     * Como a classe está configurada para incluir apenas campos explícitos,
     * esta anotação diz ao Lombok que a comparação de igualdade (equals/hashCode)
     * desta entidade deve ser feita ÚNICA E EXCLUSIVAMENTE pelo ID.
     * Ou seja, dois objetos CategoriaServico são considerados iguais se tiverem o mesmo ID.
     */
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nome;

    /*
     * @CreationTimestamp:
     * É uma anotação específica do Hibernate. Ela intercepta o momento exato
     * em que a entidade for ser salva no banco de dados PELA PRIMEIRA VEZ (INSERT)
     * e preenche este campo automaticamente com a data/hora atual do sistema.
     *
     * Isso evita que você tenha que fazer: categoria.setCreatedAt(OffsetDateTime.now())
     * manualmente no seu service. O 'updatable = false' no @Column garante que
     * essa data nunca seja alterada em um futuro UPDATE.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
