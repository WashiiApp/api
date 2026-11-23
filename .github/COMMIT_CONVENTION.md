# Convenção de commits

Use o formato observado no histórico recente do projeto:

```text
<tipo>(<escopo>): <resumo>
```

Escreva o resumo em português, com verbo no infinitivo ou descrição direta, sem ponto final. Seja específico e mantenha o assunto curto.

Tipos recomendados:

- `feat`: funcionalidade nova.
- `fix`: correção de comportamento ou segurança.
- `refactor`: reorganização sem mudança funcional intencional.
- `test`: inclusão ou ajuste de testes.
- `docs`: documentação, instruções e templates.
- `chore`: manutenção de build, ferramentas ou configuração.

O escopo deve indicar a área afetada, por exemplo `agendamento`, `cliente`, `veiculo`, `auth` ou `api`.

Exemplos:

```text
fix(veiculo): usar cliente autenticado nas operações
docs(agents): organizar instruções e templates do projeto
test(cliente): cobrir associação de veículos
```

Quando necessário, inclua corpo para explicar motivação ou impacto. Referencie issues no corpo do PR; não dependa apenas do assunto do commit para fechar uma issue.
