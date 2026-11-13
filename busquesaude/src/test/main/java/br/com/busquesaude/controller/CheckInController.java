package br.com.busquesaude.controller;

import br.com.busquesaude.dto.checkin.*;
import br.com.busquesaude.model.CheckIn;
import br.com.busquesaude.service.CheckInService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkins")
public class CheckInController {

    private final CheckInService service;

    public CheckInController(CheckInService service) { this.service = service; }

    @Operation(summary = "Fazer check-in remoto e entrar na fila de triagem")
    @PostMapping
    public ResponseEntity<CheckInResponseDTO> fazerCheckIn(@Valid @RequestBody CheckInRequestDTO request) {
        CheckIn criado = service.fazerCheckIn(converterParaModel(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(converterParaResponse(criado));
    }

    @Operation(summary = "Consultar check-in e posição atual na fila")
    @GetMapping("/{id}")
    public ResponseEntity<CheckInResponseDTO> consultar(@PathVariable Long id) {
        return ResponseEntity.ok(converterParaResponse(service.consultar(id)));
    }

    @Operation(summary = "Listar a fila de triagem de um hospital")
    @GetMapping("/hospital/{hospitalId}/fila")
    public ResponseEntity<List<CheckInResponseDTO>> fila(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(service.fila(hospitalId).stream().map(this::converterParaResponse).toList());
    }

    @Operation(summary = "Cancelar check-in (sair da fila)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        service.cancelar(id);
        return ResponseEntity.noContent().build();
    }

    private CheckIn converterParaModel(CheckInRequestDTO dto) {
        CheckIn c = new CheckIn();
        c.setPacienteId(dto.getPacienteId());
        c.setHospitalId(dto.getHospitalId());
        c.setQueixaPrincipal(dto.getQueixaPrincipal().trim());
        c.setNivelUrgencia(dto.getNivelUrgencia());
        return c;
    }

    private CheckInResponseDTO converterParaResponse(CheckIn c) {
        CheckInResponseDTO dto = new CheckInResponseDTO();
        dto.setId(c.getId());
        dto.setPaciente(c.getPacienteNome());
        dto.setHospital(c.getHospitalNome());
        dto.setNivelUrgencia(c.getNivelUrgencia());
        dto.setStatus(c.getStatus());
        dto.setPosicaoNaFila(service.posicaoNaFila(c));
        dto.setDataHora(c.getDataHora());
        return dto;
    }
}
