package br.com.busquesaude.exception;

import br.com.busquesaude.dto.error.CampoErroDTO;
import br.com.busquesaude.dto.error.ErroResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * @RestControllerAdvice: combina @ControllerAdvice + @ResponseBody. Permite CENTRALIZAR o
 * tratamento de exceções num único lugar, valendo para MÚLTIPLOS Controllers da aplicação
 * (Paciente, Hospital, CheckIn...). Assim os Controllers não precisam de try/catch e o retorno
 * é sempre JSON (ErroResponseDTO).
 *
 * @ExceptionHandler: associa um método a um tipo de exceção; quando ela é lançada em qualquer
 * Controller/Service, o Spring executa o método correspondente.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ---------- 404: recurso inexistente ----------
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponseDTO> tratarNaoEncontrado(RecursoNaoEncontradoException ex,
                                                               HttpServletRequest request) {
        return montar(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(),
                request, null, ex.getCodigo());
    }

    // ---------- 400: regra de negócio (padrão adotado nesta atividade) ----------
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponseDTO> tratarRegraNegocio(RegraNegocioException ex,
                                                              HttpServletRequest request) {
        return montar(HttpStatus.BAD_REQUEST, "Regra de negócio violada", ex.getMessage(),
                request, null, ex.getCodigo());
    }

    // ---------- 400: Jakarta Validation no @RequestBody ----------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponseDTO> tratarValidacao(MethodArgumentNotValidException ex,
                                                           HttpServletRequest request) {
        List<CampoErroDTO> campos = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new CampoErroDTO(e.getField(), e.getDefaultMessage()))
                .toList();
        return montar(HttpStatus.BAD_REQUEST, "Erro de validação",
                "Um ou mais campos são inválidos.", request, campos, "VALIDACAO_INVALIDA");
    }

    // ---------- 400: Jakarta Validation em @RequestParam / @PathVariable (ex.: lat e lon) ----------
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErroResponseDTO> tratarValidacaoParametros(HandlerMethodValidationException ex,
                                                                     HttpServletRequest request) {
        List<CampoErroDTO> campos = new ArrayList<>();
        ex.getAllValidationResults().forEach(resultado -> {
            String nome = resultado.getMethodParameter().getParameterName();
            resultado.getResolvableErrors()
                    .forEach(e -> campos.add(new CampoErroDTO(nome, e.getDefaultMessage())));
        });
        return montar(HttpStatus.BAD_REQUEST, "Erro de validação",
                "Um ou mais parâmetros são inválidos.", request, campos, "VALIDACAO_INVALIDA");
    }

    // ---------- 400: JSON malformado ou enum inexistente ----------
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponseDTO> tratarJsonInvalido(HttpMessageNotReadableException ex,
                                                              HttpServletRequest request) {
        log.warn("JSON ilegível em {}: {}", request.getRequestURI(), ex.getMessage());
        // mensagem CONTROLADA: nada de stack trace nem detalhes do Jackson.
        return montar(HttpStatus.BAD_REQUEST, "JSON inválido",
                "Não foi possível interpretar os dados enviados.", request, null, "JSON_INVALIDO");
    }

    // ---------- 400: tipo incompatível (GET /api/pacientes/abc) ----------
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponseDTO> tratarTipoIncompativel(MethodArgumentTypeMismatchException ex,
                                                                  HttpServletRequest request) {
        String mensagem = "O parâmetro '" + ex.getName() + "' possui um valor inválido.";
        return montar(HttpStatus.BAD_REQUEST, "Parâmetro inválido", mensagem,
                request, null, "PARAMETRO_INVALIDO");
    }

    // ---------- 400: parâmetro obrigatório ausente (ex.: lat sem informar) ----------
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErroResponseDTO> tratarParametroAusente(MissingServletRequestParameterException ex,
                                                                  HttpServletRequest request) {
        return montar(HttpStatus.BAD_REQUEST, "Parâmetro inválido",
                "O parâmetro '" + ex.getParameterName() + "' é obrigatório.", request, null,
                "PARAMETRO_OBRIGATORIO");
    }

    // ---------- erros de HTTP "de infraestrutura" que não devem virar 500 ----------
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResponseDTO> tratarRotaInexistente(NoResourceFoundException ex,
                                                                 HttpServletRequest request) {
        return montar(HttpStatus.NOT_FOUND, "Recurso não encontrado",
                "O endereço solicitado não existe.", request, null, "ROTA_NAO_ENCONTRADA");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResponseDTO> tratarMetodoNaoPermitido(HttpRequestMethodNotSupportedException ex,
                                                                    HttpServletRequest request) {
        return montar(HttpStatus.METHOD_NOT_ALLOWED, "Método não permitido",
                "O método HTTP utilizado não é permitido para este endereço.", request, null,
                "METODO_NAO_PERMITIDO");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErroResponseDTO> tratarMediaType(HttpMediaTypeNotSupportedException ex,
                                                           HttpServletRequest request) {
        return montar(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Tipo de mídia não suportado",
                "Envie o conteúdo no formato application/json.", request, null,
                "MEDIA_TYPE_NAO_SUPORTADO");
    }

    // ---------- 500: qualquer outra falha ----------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponseDTO> tratarErroInterno(Exception ex, HttpServletRequest request) {
        // Detalhes (stack trace) ficam SOMENTE no log do servidor, nunca na resposta HTTP.
        log.error("Erro inesperado em {} {}", request.getMethod(), request.getRequestURI(), ex);
        return montar(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno",
                "Ocorreu um erro inesperado.", request, null, "ERRO_INTERNO");
    }

    private ResponseEntity<ErroResponseDTO> montar(HttpStatus status, String erro, String mensagem,
                                                   HttpServletRequest request,
                                                   List<CampoErroDTO> campos, String codigo) {
        ErroResponseDTO corpo = new ErroResponseDTO(status.value(), erro, mensagem,
                request.getRequestURI(), campos, codigo);
        return ResponseEntity.status(status).body(corpo);
    }
}
