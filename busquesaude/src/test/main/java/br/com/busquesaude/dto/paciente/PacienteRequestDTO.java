package br.com.busquesaude.dto.paciente;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;
import java.time.LocalDate;

/** Pré-cadastro do paciente (entrada). Só os campos abaixo podem ser enviados pelo cliente. */
public class PacienteRequestDTO {

    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, max = 120, message = "O nome deve possuir entre 3 e 120 caracteres.")
    private String nome;

    @NotBlank(message = "O CPF é obrigatório.")
    @CPF(message = "Informe um CPF válido.")
    private String cpf;

    @NotNull(message = "A data de nascimento é obrigatória.")
    @Past(message = "A data de nascimento deve estar no passado.")
    private LocalDate dataNascimento;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Informe um e-mail válido.")
    private String email;

    @NotBlank(message = "O telefone é obrigatório.")
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$",
             message = "Informe um telefone válido. Ex.: (65) 99999-9999")
    private String telefone;

    @Size(max = 60, message = "O convênio deve possuir no máximo 60 caracteres.")
    private String convenio; // opcional

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getConvenio() { return convenio; }
    public void setConvenio(String convenio) { this.convenio = convenio; }
}
