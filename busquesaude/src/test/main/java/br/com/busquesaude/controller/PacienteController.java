package br.com.busquesaude.controller;

import br.com.busquesaude.dto.paciente.*;
import br.com.busquesaude.model.Paciente;
import br.com.busquesaude.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService service;

    public PacienteController(PacienteService service) { this.service = service; }

    // @Valid ativa as anotações do DTO. Se algo for inválido, o Spring responde 400
    // e este método nem chega a ser executado.
    @Operation(summary = "Pré-cadastro do paciente")
    @PostMapping
    public ResponseEntity<PacienteResponseDTO> cadastrar(@Valid @RequestBody PacienteRequestDTO request) {
        Paciente cadastrado = service.cadastrar(converterParaModel(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(converterParaResponse(cadastrado));
    }

    @Operation(summary = "Consultar paciente por ID")
    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> consultar(@PathVariable Long id) {
        return ResponseEntity.ok(converterParaResponse(service.consultar(id)));
    }

    @Operation(summary = "Listar pacientes")
    @GetMapping
    public ResponseEntity<List<PacienteResponseDTO>> listar() {
        List<PacienteResponseDTO> pacientes = service.listar().stream().map(this::converterParaResponse).toList();
        return ResponseEntity.ok(pacientes);
    }

    @Operation(summary = "Atualizar paciente")
    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> atualizar(@PathVariable Long id,
                                                         @Valid @RequestBody PacienteRequestDTO request) {
        return ResponseEntity.ok(converterParaResponse(service.atualizar(id, converterParaModel(request))));
    }

    @Operation(summary = "Excluir paciente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    private Paciente converterParaModel(PacienteRequestDTO dto) {
        Paciente p = new Paciente();
        p.setNome(dto.getNome().trim());
        p.setCpf(dto.getCpf().replaceAll("\\D", "")); // guarda só os dígitos
        p.setDataNascimento(dto.getDataNascimento());
        p.setEmail(dto.getEmail().trim().toLowerCase());
        p.setTelefone(dto.getTelefone());
        p.setConvenio(dto.getConvenio());
        return p;
    }

    private PacienteResponseDTO converterParaResponse(Paciente p) {
        PacienteResponseDTO dto = new PacienteResponseDTO();
        dto.setId(p.getId());
        dto.setNome(p.getNome());
        dto.setCpf(p.getCpf());
        dto.setDataNascimento(p.getDataNascimento());
        dto.setEmail(p.getEmail());
        dto.setTelefone(p.getTelefone());
        dto.setConvenio(p.getConvenio());
        return dto;
    }
}
