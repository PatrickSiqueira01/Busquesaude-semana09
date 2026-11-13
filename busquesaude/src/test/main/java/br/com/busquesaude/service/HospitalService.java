package br.com.busquesaude.service;

import br.com.busquesaude.model.Hospital;
import br.com.busquesaude.exception.RecursoNaoEncontradoException;
import br.com.busquesaude.exception.RegraNegocioException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class HospitalService {

    private final Map<Long, Hospital> hospitais = new LinkedHashMap<>();
    private final AtomicLong sequencia = new AtomicLong(1);

    public synchronized Hospital cadastrar(Hospital hospital) {
        boolean duplicado = hospitais.values().stream().anyMatch(h -> h.getCnpj().equals(hospital.getCnpj()));
        if (duplicado) throw new RegraNegocioException("CNPJ_JA_CADASTRADO", "Já existe hospital com este CNPJ.");
        hospital.setId(sequencia.getAndIncrement());
        hospitais.put(hospital.getId(), hospital);
        return hospital;
    }

    public synchronized Hospital consultar(Long id) {
        Hospital hospital = hospitais.get(id);
        if (hospital == null)
            throw new RecursoNaoEncontradoException("HOSPITAL_NAO_ENCONTRADO", "Hospital não encontrado.");
        return hospital;
    }

    public synchronized void excluir(Long id) {
        consultar(id); // lança RecursoNaoEncontradoException se não existir
        hospitais.remove(id);
    }

    public synchronized List<Hospital> listar() { return new ArrayList<>(hospitais.values()); }

    /** Hospitais que atendem a especialidade (ou todos, se não informada). */
    public synchronized List<Hospital> listarPorEspecialidade(String especialidade) {
        if (especialidade == null || especialidade.isBlank()) return listar();
        String busca = especialidade.toLowerCase();
        return hospitais.values().stream()
                .filter(h -> h.getEspecialidades().toLowerCase().contains(busca))
                .toList();
    }

    /** Distância em km entre um ponto e o hospital (fórmula de Haversine). */
    public double distanciaKm(Hospital h, double lat, double lon) {
        double dLat = Math.toRadians(h.getLatitude() - lat);
        double dLon = Math.toRadians(h.getLongitude() - lon);
        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(h.getLatitude()))
                * Math.pow(Math.sin(dLon / 2), 2);
        return 6371 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
