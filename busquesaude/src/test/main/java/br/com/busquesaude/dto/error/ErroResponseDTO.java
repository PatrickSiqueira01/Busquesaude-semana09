package br.com.busquesaude.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Formato ÚNICO de resposta de erro de toda a API. */
public class ErroResponseDTO {
    private LocalDateTime timestamp;
    private Integer status;
    private String erro;
    private String mensagem;
    private String path;
    private List<CampoErroDTO> campos;

    // Desafio adicional (Parte 14): código estável, opcional, para o Front-End.
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String codigo;

    public ErroResponseDTO(Integer status, String erro, String mensagem, String path,
                           List<CampoErroDTO> campos, String codigo) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.erro = erro;
        this.mensagem = mensagem;
        this.path = path;
        this.campos = campos == null ? new ArrayList<>() : campos;
        this.codigo = codigo;
    }

    public ErroResponseDTO(Integer status, String erro, String mensagem, String path) {
        this(status, erro, mensagem, path, null, null);
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public Integer getStatus() { return status; }
    public String getErro() { return erro; }
    public String getMensagem() { return mensagem; }
    public String getPath() { return path; }
    public List<CampoErroDTO> getCampos() { return campos; }
    public String getCodigo() { return codigo; }
}
