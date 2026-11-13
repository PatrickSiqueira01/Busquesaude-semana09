# Atividade avaliativa — Semana 09: Tratamento global de exceções
Projeto: **BusqueSaúde** (adaptação do "Sistema Acadêmico": Aluno → Paciente, Curso → Hospital, Matrícula → Check-in).

## Adaptações em relação ao enunciado
| Enunciado | BusqueSaúde |
|---|---|
| Aluno / `/api/alunos/{id}` | Paciente / `/api/pacientes/{id}` |
| Matrícula ativa duplicada | Paciente já está na fila do hospital (`/api/checkins`) |
| `status: ABERTA` em `StatusMatricula` | `nivelUrgencia: DESCONHECIDA` em `NivelUrgencia` |
| UsuarioController / CursoController | não existem; revisados Paciente, Hospital e CheckIn controllers |
| PostgreSQL + Flyway | o projeto ainda usa armazenamento em memória (não há Repository/JPA) — não foi alterado |

## Atividade 34 — Tabela de códigos HTTP
| Situação | Status |
|---|---|
| Paciente inexistente | 404 Not Found |
| DTO inválido | 400 Bad Request |
| Regra de negócio | 400 Bad Request |
| JSON inválido | 400 Bad Request |
| Parâmetro incompatível | 400 Bad Request |
| Erro inesperado | 500 Internal Server Error |

## Testes (Swagger — http://localhost:8080/swagger-ui.html)
| # | Requisição | Esperado |
|---|---|---|
| 29 | `GET /api/pacientes/99999` | 404 |
| 30 | `POST /api/pacientes` com o mesmo CPF duas vezes, ou `POST /api/checkins` repetido (mesmo paciente e hospital) | 400 |
| 31 | `POST /api/pacientes` `{"nome":"","email":"x"}` | 400 com lista `campos` |
| 32 | `GET /api/pacientes/abc` | 400 |
| 33 | `POST /api/checkins` com `"nivelUrgencia":"DESCONHECIDA"` | 400 |

Os mesmos cenários estão automatizados em `ErroPadronizadoTest`.

## Parte 13 — Análise conceitual
1. **`throw new RecursoNaoEncontradoException(...)` x `404`**: a exceção é um mecanismo da linguagem/camada de negócio, lançado pelo Service para dizer *o que* aconteceu, sem conhecer HTTP. O 404 é o código do protocolo HTTP enviado ao cliente. O `GlobalExceptionHandler` faz a ponte entre os dois.
2. **try/catch em todos os Controllers**: gera código repetido e inconsistente (cada um responde de um jeito), mistura responsabilidades, é fácil esquecer um caso e dificulta manutenção. Centralizar deixa o Controller só com o fluxo feliz.
3. **`@RestControllerAdvice`**: classe que centraliza o tratamento de exceções para todos os Controllers (`@ControllerAdvice` + `@ResponseBody`), devolvendo JSON.
4. **`@ExceptionHandler`**: marca o método que trata um tipo específico de exceção; o Spring o chama quando ela é lançada.
5. **Padrão de erro**: o cliente/Front-End consegue tratar qualquer erro da mesma forma (mesmos campos), facilita documentação, depuração, testes e integração.
6. **`CampoErroDTO`**: informa *qual campo* falhou e *por quê*, permitindo ao Front-End destacar cada campo do formulário e exibir todos os erros de uma vez.
7. **400 x 404**: 400 = a requisição está errada/inválida (dados, formato, regra); 404 = a requisição é válida, mas o recurso pedido não existe.
8. **409 Conflict**: conflito com o estado atual do recurso — CPF/CNPJ já cadastrado, paciente já na fila, edição concorrente (versão desatualizada), exclusão de recurso com dependências. (Nesta atividade foi adotado 400 para regras de negócio, como pede o enunciado.)
9. **Stack trace**: expõe classes, pacotes, versões, SQL e caminhos internos, ajudando atacantes (vazamento de informação), além de ser incompreensível para o usuário.
10. **Log x resposta HTTP**: o log fica no servidor, é para desenvolvedores/operação e pode conter todos os detalhes técnicos (stack trace). A resposta HTTP vai ao cliente e deve conter só uma mensagem controlada e segura.

## Parte 14 — Desafio: campo `codigo`
`ErroResponseDTO` possui `codigo` (omitido do JSON quando nulo). Exemplo:
```json
{ "timestamp": "2026-11-09T15:00:00", "status": 404, "erro": "Recurso não encontrado",
  "mensagem": "Paciente não encontrado.", "path": "/api/pacientes/999",
  "campos": [], "codigo": "PACIENTE_NAO_ENCONTRADO" }
```
**Ajuda ao Front-End:** a `mensagem` pode mudar de texto ou idioma, mas o `codigo` é estável. O Front pode usar um `switch` no código para escolher o comportamento (ex.: `CPF_JA_CADASTRADO` → destacar o campo CPF; `PACIENTE_JA_NA_FILA` → redirecionar para a tela da fila), traduzir/internacionalizar mensagens e registrar métricas, sem depender de comparar textos.

## Parte 15 — Estrutura
```
dto/error/        CampoErroDTO, ErroResponseDTO
exception/        GlobalExceptionHandler, RegraNegocioException, RecursoNaoEncontradoException
```

## Parte 16 — Checklist
- [x] GlobalExceptionHandler criado · [x] @RestControllerAdvice · [x] @ExceptionHandler
- [x] 404 · [x] regra de negócio · [x] Validation · [x] enum inválido · [x] parâmetro inválido · [x] 500
- [x] ErroResponseDTO · [x] CampoErroDTO · [x] Controllers sem try/catch/null-check repetitivo
- [x] Respostas JSON padronizadas · [ ] Swagger testado manualmente (faça no seu ambiente)
- [ ] PostgreSQL/Flyway: não se aplica — o BusqueSaúde ainda usa dados em memória
