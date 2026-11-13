package br.com.busquesaude.dto.hospital;

/** Resultado da busca "hospital mais adequado": distância e tamanho atual da fila. */
public class HospitalProximoResponseDTO {
    private Long id;
    private String nome;
    private String endereco;
    private String especialidades;
    private Double distanciaKm;
    private Long pessoasNaFila;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getEspecialidades() { return especialidades; }
    public void setEspecialidades(String especialidades) { this.especialidades = especialidades; }
    public Double getDistanciaKm() { return distanciaKm; }
    public void setDistanciaKm(Double distanciaKm) { this.distanciaKm = distanciaKm; }
    public Long getPessoasNaFila() { return pessoasNaFila; }
    public void setPessoasNaFila(Long pessoasNaFila) { this.pessoasNaFila = pessoasNaFila; }
}
