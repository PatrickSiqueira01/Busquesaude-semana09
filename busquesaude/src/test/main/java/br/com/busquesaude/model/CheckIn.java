package br.com.busquesaude.model;

import java.time.LocalDateTime;

public class CheckIn {
    private Long id;
    private Long pacienteId;
    private String pacienteNome;
    private Long hospitalId;
    private String hospitalNome;
    private String queixaPrincipal;
    private NivelUrgencia nivelUrgencia;
    private StatusCheckIn status = StatusCheckIn.AGUARDANDO_TRIAGEM;
    private LocalDateTime dataHora = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }
    public String getPacienteNome() { return pacienteNome; }
    public void setPacienteNome(String pacienteNome) { this.pacienteNome = pacienteNome; }
    public Long getHospitalId() { return hospitalId; }
    public void setHospitalId(Long hospitalId) { this.hospitalId = hospitalId; }
    public String getHospitalNome() { return hospitalNome; }
    public void setHospitalNome(String hospitalNome) { this.hospitalNome = hospitalNome; }
    public String getQueixaPrincipal() { return queixaPrincipal; }
    public void setQueixaPrincipal(String queixaPrincipal) { this.queixaPrincipal = queixaPrincipal; }
    public NivelUrgencia getNivelUrgencia() { return nivelUrgencia; }
    public void setNivelUrgencia(NivelUrgencia nivelUrgencia) { this.nivelUrgencia = nivelUrgencia; }
    public StatusCheckIn getStatus() { return status; }
    public void setStatus(StatusCheckIn status) { this.status = status; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
