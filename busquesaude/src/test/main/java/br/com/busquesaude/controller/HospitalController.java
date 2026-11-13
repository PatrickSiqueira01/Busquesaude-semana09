package br.com.busquesaude.controller;

import br.com.busquesaude.dto.hospital.*;
import br.com.busquesaude.model.Hospital;
import br.com.busquesaude.service.CheckInService;
import br.com.busquesaude.service.HospitalService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/hospitais")
public class HospitalController {

    private final HospitalService service;
    private final CheckInService checkInService;

    public HospitalController(HospitalService service, CheckInService checkInService) {
        this.service = service;
        this.checkInService = checkInService;
    }

    @Operation(summary = "Cadastrar hospital")
    @PostMapping
    public ResponseEntity<HospitalResponseDTO> cadastrar(@Valid @RequestBody HospitalRequestDTO request) {
        Hospital cadastrado = service.cadastrar(converterParaModel(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(converterParaResponse(cadastrado));
    }

    @Operation(summary = "Consultar hospital por ID")
    @GetMapping("/{id}")
    public ResponseEntity<HospitalResponseDTO> consultar(@PathVariable Long id) {
        return ResponseEntity.ok(converterParaResponse(service.consultar(id)));
    }

    @Operation(summary = "Listar hospitais")
    @GetMapping
    public ResponseEntity<List<HospitalResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar().stream().map(this::converterParaResponse).toList());
    }

    /** Localizar o hospital mais adequado: por distância, especialidade e tamanho da fila. */
    @Operation(summary = "Localizar hospitais por distância, especialidade e tamanho da fila")
    @GetMapping("/proximos")
    public ResponseEntity<List<HospitalProximoResponseDTO>> proximos(
            @RequestParam @DecimalMin(value = "-90.0", message = "A latitude mínima é -90.")
                          @DecimalMax(value = "90.0", message = "A latitude máxima é 90.") double lat,
            @RequestParam @DecimalMin(value = "-180.0", message = "A longitude mínima é -180.")
                          @DecimalMax(value = "180.0", message = "A longitude máxima é 180.") double lon,
            @RequestParam(required = false) @Size(max = 60, message = "Especialidade muito longa.") String especialidade) {

        List<HospitalProximoResponseDTO> resultado = service.listarPorEspecialidade(especialidade).stream()
                .map(h -> {
                    HospitalProximoResponseDTO dto = new HospitalProximoResponseDTO();
                    dto.setId(h.getId());
                    dto.setNome(h.getNome());
                    dto.setEndereco(h.getEndereco());
                    dto.setEspecialidades(h.getEspecialidades());
                    dto.setDistanciaKm(Math.round(service.distanciaKm(h, lat, lon) * 10) / 10.0);
                    dto.setPessoasNaFila(checkInService.pessoasNaFila(h.getId()));
                    return dto;
                })
                .sorted(Comparator.comparing(HospitalProximoResponseDTO::getDistanciaKm))
                .toList();
        return ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Excluir hospital")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    private Hospital converterParaModel(HospitalRequestDTO dto) {
        Hospital h = new Hospital();
        h.setNome(dto.getNome().trim());
        h.setCnpj(dto.getCnpj().replaceAll("\\D", ""));
        h.setEndereco(dto.getEndereco());
        h.setCidade(dto.getCidade());
        h.setUf(dto.getUf());
        h.setLatitude(dto.getLatitude());
        h.setLongitude(dto.getLongitude());
        h.setEspecialidades(dto.getEspecialidades());
        return h;
    }

    private HospitalResponseDTO converterParaResponse(Hospital h) {
        HospitalResponseDTO dto = new HospitalResponseDTO();
        dto.setId(h.getId());
        dto.setNome(h.getNome());
        dto.setCnpj(h.getCnpj());
        dto.setEndereco(h.getEndereco());
        dto.setCidade(h.getCidade());
        dto.setUf(h.getUf());
        dto.setLatitude(h.getLatitude());
        dto.setLongitude(h.getLongitude());
        dto.setEspecialidades(h.getEspecialidades());
        return dto;
    }
}
