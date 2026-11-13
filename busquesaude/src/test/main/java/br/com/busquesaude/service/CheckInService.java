package br.com.busquesaude.service;

import br.com.busquesaude.model.*;
import br.com.busquesaude.exception.RecursoNaoEncontradoException;
import br.com.busquesaude.exception.RegraNegocioException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CheckInService {

    private final Map<Long, CheckIn> checkIns = new LinkedHashMap<>();
    private final AtomicLong sequencia = new AtomicLong(1);
    private final PacienteService pacienteService;
    private final HospitalService hospitalService;

    public CheckInService(PacienteService pacienteService, HospitalService hospitalService) {
        this.pacienteService = pacienteService;
        this.hospitalService = hospitalService;
    }

    public synchronized CheckIn fazerCheckIn(CheckIn checkIn) {
        // Regras de negócio: paciente e hospital precisam existir; sem fila duplicada.
        Paciente paciente = pacienteService.consultar(checkIn.getPacienteId());   // 404 se não existir
        Hospital hospital = hospitalService.consultar(checkIn.getHospitalId());   // 404 se não existir
        boolean jaNaFila = checkIns.values().stream().anyMatch(c ->
                c.getPacienteId().equals(paciente.getId())
                && c.getHospitalId().equals(hospital.getId())
                && c.getStatus() == StatusCheckIn.AGUARDANDO_TRIAGEM);
        if (jaNaFila)
            throw new RegraNegocioException("PACIENTE_JA_NA_FILA", "O paciente já está na fila deste hospital.");

        checkIn.setId(sequencia.getAndIncrement());
        checkIn.setPacienteNome(paciente.getNome());
        checkIn.setHospitalNome(hospital.getNome());
        checkIns.put(checkIn.getId(), checkIn);
        return checkIn;
    }

    public synchronized CheckIn consultar(Long id) {
        CheckIn checkIn = checkIns.get(id);
        if (checkIn == null)
            throw new RecursoNaoEncontradoException("CHECKIN_NAO_ENCONTRADO", "Check-in não encontrado.");
        return checkIn;
    }

    /** Paciente desiste da fila: o check-in passa a CANCELADO e deixa de ocupar posição. */
    public synchronized void cancelar(Long id) {
        consultar(id).setStatus(StatusCheckIn.CANCELADO);
    }

    /** Fila de triagem: maior urgência primeiro; empate resolvido por ordem de chegada. */
    public synchronized List<CheckIn> fila(Long hospitalId) {
        return checkIns.values().stream()
                .filter(c -> c.getHospitalId().equals(hospitalId) && c.getStatus() == StatusCheckIn.AGUARDANDO_TRIAGEM)
                .sorted(Comparator.comparing((CheckIn c) -> c.getNivelUrgencia().ordinal()).reversed()
                        .thenComparing(CheckIn::getId))
                .toList();
    }

    public synchronized int posicaoNaFila(CheckIn checkIn) {
        List<CheckIn> fila = fila(checkIn.getHospitalId());
        for (int i = 0; i < fila.size(); i++) {
            if (fila.get(i).getId().equals(checkIn.getId())) return i + 1;
        }
        return 0; // não está mais aguardando
    }

    public synchronized long pessoasNaFila(Long hospitalId) { return fila(hospitalId).size(); }
}
