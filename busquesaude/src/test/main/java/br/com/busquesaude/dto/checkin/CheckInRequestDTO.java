package br.com.busquesaude.dto.checkin;

import br.com.busquesaude.model.NivelUrgencia;
import jakarta.validation.constraints.*;

public class CheckInRequestDTO {

    @NotNull(message = "O id do paciente é obrigatório.")
    @Positive(message = "O id do paciente deve ser maior que zero.")
    private Long pacienteId;

    @NotNull(message = "O id do hospital é obrigatório.")
    @Positive(message = "O id do hospital deve ser maior que zero.")
    private Long hospitalId;

    @NotBlank(message = "Descreva brevemente o motivo do atendimento.")
    @Size(min = 5, max = 300, message = "A queixa deve possuir entre 5 e 300 caracteres.")
    private String queixaPrincipal;

    @NotNull(message = "O nível de urgência é obrigatório (BAIXA, MEDIA ou ALTA).")
    private NivelUrgencia nivelUrgencia;

    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }
    public Long getHospitalId() { return hospitalId; }
    public void setHospitalId(Long hospitalId) { this.hospitalId = hospitalId; }
    public String getQueixaPrincipal() { return queixaPrincipal; }
    public void setQueixaPrincipal(String queixaPrincipal) { this.queixaPrincipal = queixaPrincipal; }
    public NivelUrgencia getNivelUrgencia() { return nivelUrgencia; }
    public void setNivelUrgencia(NivelUrgencia nivelUrgencia) { this.nivelUrgencia = nivelUrgencia; }
}
