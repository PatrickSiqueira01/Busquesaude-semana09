package br.com.busquesaude.dto.hospital;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CNPJ;

public class HospitalRequestDTO {

    @NotBlank(message = "O nome do hospital é obrigatório.")
    @Size(min = 3, max = 150, message = "O nome deve possuir entre 3 e 150 caracteres.")
    private String nome;

    @NotBlank(message = "O CNPJ é obrigatório.")
    @CNPJ(message = "Informe um CNPJ válido.")
    private String cnpj;

    @NotBlank(message = "O endereço é obrigatório.")
    private String endereco;

    @NotBlank(message = "A cidade é obrigatória.")
    private String cidade;

    @NotBlank(message = "A UF é obrigatória.")
    @Pattern(regexp = "^[A-Z]{2}$", message = "A UF deve ter 2 letras maiúsculas. Ex.: MT")
    private String uf;

    @NotNull(message = "A latitude é obrigatória.")
    @DecimalMin(value = "-90.0", message = "A latitude mínima é -90.")
    @DecimalMax(value = "90.0", message = "A latitude máxima é 90.")
    private Double latitude;

    @NotNull(message = "A longitude é obrigatória.")
    @DecimalMin(value = "-180.0", message = "A longitude mínima é -180.")
    @DecimalMax(value = "180.0", message = "A longitude máxima é 180.")
    private Double longitude;

    @NotBlank(message = "Informe ao menos uma especialidade (separadas por vírgula).")
    private String especialidades;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getEspecialidades() { return especialidades; }
    public void setEspecialidades(String especialidades) { this.especialidades = especialidades; }
}
