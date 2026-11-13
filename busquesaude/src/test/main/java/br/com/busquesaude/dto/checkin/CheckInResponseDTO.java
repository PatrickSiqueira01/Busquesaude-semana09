package br.com.busquesaude.dto.checkin;

import br.com.busquesaude.model.NivelUrgencia;
import br.com.busquesaude.model.StatusCheckIn;
import java.time.LocalDateTime;

public class CheckInResponseDTO {
    private Long id;
    private String paciente;
    private String hospital;
    private NivelUrgencia nivelUrgencia;
    private StatusCheckIn status;
    private Integer posicaoNaFila;
    private LocalDateTime dataHora;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPaciente() { return paciente; }
    public void setPaciente(String paciente) { this.paciente = paciente; }
    public String getHospital() { return hospital; }
    public void setHospital(String hospital) { this.hospital = hospital; }
    public NivelUrgencia getNivelUrgencia() { return nivelUrgencia; }
    public void setNivelUrgencia(NivelUrgencia nivelUrgencia) { this.nivelUrgencia = nivelUrgencia; }
    public StatusCheckIn getStatus() { return status; }
    public void setStatus(StatusCheckIn status) { this.status = status; }
    public Integer getPosicaoNaFila() { return posicaoNaFila; }
    public void setPosicaoNaFila(Integer posicaoNaFila) { this.posicaoNaFila = posicaoNaFila; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
