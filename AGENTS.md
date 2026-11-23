# Instruções para agentes

## Contexto do projeto

- Esta é a API REST do Washii, construída com Java 25, Spring Boot e Maven.
- O código de produção fica em `src/main/java/br/com/washii/api`; testes ficam em `src/test/java`.
- Controllers cuidam do HTTP, services aplicam regras de negócio, repositories acessam dados e DTOs definem os contratos da API.

## Orientações de implementação

- Antes de alterar código, leia o controller, service, entidade, repository e testes relacionados à tarefa.
- Preserve autenticação e autorização: obtenha a identidade do usuário a partir de `Authentication`, nunca confie em um ID de proprietário enviado pelo cliente.
- Mantenha regras de negócio na camada de serviço e use as exceções existentes em `exception/` para respostas consistentes.
- Prefira mudanças pequenas e compatíveis com os contratos existentes. Valide entradas com Jakarta Validation quando apropriado.
- Não inclua segredos, tokens, credenciais ou dados pessoais em commits.
- Organize novas issues em `.github/ISSUE_TEMPLATE/` e use `.github/PULL_REQUEST_TEMPLATE.md` para PRs.

## Commits e entrega

- Siga `.github/COMMIT_CONVENTION.md`: `<tipo>(<escopo>): <resumo>`.
- Use branches curtas e descritivas, com o prefixo `codex/` para trabalho de agentes.
- Vincule a issue no PR e descreva comportamento, impacto em API e validação realizada.

## Comandos úteis

- Compilar: `./mvnw -DskipTests compile` (Windows: `mvnw.cmd -DskipTests compile`).
- Testar: `./mvnw test` (Windows: `mvnw.cmd test`).
- Testes de integração podem depender de serviços externos, como PostgreSQL ou Redis; confira o teste e a configuração antes de executá-los.
