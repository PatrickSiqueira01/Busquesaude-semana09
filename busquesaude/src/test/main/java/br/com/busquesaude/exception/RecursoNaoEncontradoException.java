package br.com.busquesaude.exception;

/** Lançada pelo Service quando um recurso (paciente, hospital, check-in) não existe. Vira 404. */
public class RecursoNaoEncontradoException extends RuntimeException {
    private final String codigo;

    public RecursoNaoEncontradoException(String mensagem) { this(null, mensagem); }

    public RecursoNaoEncontradoException(String codigo, String mensagem) {
        super(mensagem);
        this.codigo = codigo;
    }

    public String getCodigo() { return codigo; }
}
