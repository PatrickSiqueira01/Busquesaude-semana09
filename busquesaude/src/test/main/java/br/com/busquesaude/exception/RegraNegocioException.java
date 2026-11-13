package br.com.busquesaude.exception;

/** Lançada pelo Service quando uma regra de negócio é violada (CPF duplicado, já está na fila...). Vira 400. */
public class RegraNegocioException extends RuntimeException {
    private final String codigo;

    public RegraNegocioException(String mensagem) { this(null, mensagem); }

    public RegraNegocioException(String codigo, String mensagem) {
        super(mensagem);
        this.codigo = codigo;
    }

    public String getCodigo() { return codigo; }
}
