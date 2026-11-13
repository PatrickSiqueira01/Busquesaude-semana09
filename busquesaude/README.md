# BusqueSaúde — MVP da API (Projeto Integrador, Programação Web II — Semana 04)

Pré-cadastro do paciente, busca do hospital mais adequado e check-in remoto na fila de triagem.
Esta etapa aplica **DTOs + Jakarta Validation (`@Valid`)** sobre a API REST da Semana 03.
Os dados ficam em memória (a camada Repository/JPA entra na Semana 05).

## Rodar
    mvn spring-boot:run     # http://localhost:8080  |  Swagger: http://localhost:8080/swagger-ui.html
    mvn test

## Estrutura (conforme a apostila)
    br.com.busquesaude
    +-- controller   -> @Valid + converterParaModel / converterParaResponse
    +-- service      -> regras de negócio (CPF/CNPJ duplicado, paciente/hospital existentes, fila)
    +-- dto          -> paciente/ hospital/ checkin/  (RequestDTO validado + ResponseDTO)
    +-- model        -> Paciente, Hospital, CheckIn (sem anotações de validação)

## Validação estrutural (DTO) x regra de negócio (Service)
| Estrutural → 400 | Negócio → 409/404 |
|---|---|
| nome obrigatório, tamanho 3–120 | CPF já cadastrado |
| e-mail com formato válido | CNPJ já cadastrado |
| CPF/CNPJ válidos, telefone, data no passado | paciente já está na fila do hospital |
| ids positivos, urgência obrigatória | paciente/hospital inexistente |
| latitude -90..90, longitude -180..180 | |

## Endpoints
| Método | Rota |
|---|---|
| POST / GET / PUT / DELETE | /api/pacientes, /api/pacientes/{id} (DELETE → 204 ou 404) |
| POST / GET / DELETE | /api/hospitais, /api/hospitais/{id} |
| GET | /api/hospitais/proximos?lat=&lon=&especialidade= |
| POST | /api/checkins (entra na fila) |
| GET | /api/checkins/{id}, /api/checkins/hospital/{id}/fila |
| DELETE | /api/checkins/{id} (cancela o check-in e sai da fila → 204 ou 404) |

Todos os endpoints têm `@Operation` (Swagger/OpenAPI), como pede a apostila da Semana 03.

## Exemplos (mesmos 3 casos da aula)
1. Válido → 201

       {"nome":"Ana Souza","cpf":"529.982.247-25","dataNascimento":"1995-03-10",
        "email":"ana@email.com","telefone":"(65) 99999-9999"}
2. Nome vazio → 400  (`"nome": ""`)
3. E-mail inválido → 400  (`"email": "ana"`)

CPF válido para teste: 529.982.247-25 (ou 111.444.777-35). CNPJ válido para teste: 11.222.333/0001-81.

O corpo do 400 traz a lista de campos inválidos (`server.error.include-binding-errors=always`).
A padronização detalhada com `@ControllerAdvice` fica para uma aula futura, como indica a apostila.

## Semana 09 — Tratamento global de exceções
`exception/GlobalExceptionHandler` (`@RestControllerAdvice`) padroniza todos os erros em `ErroResponseDTO`
(`timestamp, status, erro, mensagem, path, campos, codigo`). Regras de negócio agora retornam **400** (antes 409).
Veja `RESPOSTAS.md` para a tabela de status, os testes e as respostas conceituais.
