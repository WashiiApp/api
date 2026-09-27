package br.com.washii.api.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cliente")
@PrimaryKeyJoinColumn(name = "id_usuario") // <-- Indica qual o nome da FK na tabela cliente que aponta para usuario
@Data
@AllArgsConstructor
@NoArgsConstructor
/*
 * @EqualsAndHashCode(callSuper = true):
 * Como a classe Cliente herda de Usuario (extends Usuario), o atributo 'callSuper = true'
 * instrui o Lombok a chamar os métodos equals() e hashCode() da classe pai durante a geração.
 *
 * - O que isso significa na prática?
 * Dois objetos da classe Cliente só serão considerados iguais se os seus próprios atributos
 * (cpf, sobrenome, primeiroNome) E TAMBÉM os atributos herdados da classe Usuario (ex: id, email, etc.)
 * forem exatamente iguais.
 *
 * Se fosse 'callSuper = false' (que é o padrão), o Lombok ignoraria a classe pai e compararia
 * apenas os campos específicos da classe Cliente.
 */
@EqualsAndHashCode(callSuper = true)
public class Cliente extends Usuario{

    @Column(unique = true, nullable = false, length = 11)
    private String cpf;

    @Column(name = "sobrenome", nullable = false, length = 100)
    private String sobreNome;

    @Column(name = "primeiro_nome", nullable = false, length = 100)
    private String primeiroNome;
}
