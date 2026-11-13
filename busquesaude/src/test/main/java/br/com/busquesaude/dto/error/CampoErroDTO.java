package br.com.busquesaude.dto.error;

/** Representa UM campo inválido (usado nos erros de Jakarta Validation). */
public class CampoErroDTO {
    private String campo;
    private String mensagem;

    public CampoErroDTO(String campo, String mensagem) {
        this.campo = campo;
        this.mensagem = mensagem;
    }

    public String getCampo() { return campo; }
    public String getMensagem() { return mensagem; }
}
