package br.com.washii.api.model;


import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point; // Vem da dependencia hibernate-spatial

@Entity
@Table(name = "lava_jato")
@PrimaryKeyJoinColumn(name = "id_usuario") // Indica a relação com a tabela pai
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LavaJato extends Usuario {

    @Column(name = "razao_social", nullable = false, length = 200)
    private String razaoSocial;

    @Column(name = "nome_fantasia", nullable = false, length = 150)
    private String nomeFantasia;

    @Column(name = "fluxo_simultaneo", nullable = false)
    private Integer fluxoSimultaneo = 1;

    @Column(nullable = false, unique = true, length = 18)
    private String cnpj;

    @Column(nullable = false, length = 10)
    private String numero;

    @Column(nullable = false, length = 150)
    private String logradouro;

    @Column(nullable = false, length = 100)
    private String bairro;

    @Column(nullable = false, length = 9)
    private String cep;

    @Column(columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point coordenadas;
}
