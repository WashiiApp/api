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

    /*
     * org.locationtech.jts.geom.Point:
     * O JTS (Java Topology Suite) é uma biblioteca padrão para trabalhar com geometria espacial em Java.
     * Quando usamos a dependência 'hibernate-spatial', o Hibernate consegue mapear esses objetos
     * espaciais do Java para tipos espaciais nativos do banco de dados (como o PostGIS no PostgreSQL).
     * Um 'Point' representa um ponto exato no mapa, composto por uma Longitude (X) e uma Latitude (Y).
     *
     * columnDefinition = "geometry(Point, 4326)":
     * - geometry: Indica ao banco de dados que a coluna armazenará dados espaciais.
     * - Point: Restringe essa coluna para aceitar apenas pontos (não aceita polígonos, linhas, etc).
     * - 4326: É o SRID (Spatial Reference System Identifier). O número 4326 representa o sistema
     *   WGS 84, que é o padrão global utilizado por GPS, Google Maps e smartphones para
     *   representar latitude e longitude na Terra.
     */
    @Column(columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point coordenadas;
}
