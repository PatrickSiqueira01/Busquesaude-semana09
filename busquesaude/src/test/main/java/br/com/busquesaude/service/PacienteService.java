package br.com.busquesaude.service;

import br.com.busquesaude.model.Paciente;
import br.com.busquesaude.exception.RecursoNaoEncontradoException;
import br.com.busquesaude.exception.RegraNegocioException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PacienteService {

    // Armazenamento em memória: a camada Repository/JPA entra na Semana 05.
    private final Map<Long, Paciente> pacientes = new LinkedHashMap<>();
    private final AtomicLong sequencia = new AtomicLong(1);

    public synchronized Paciente cadastrar(Paciente paciente) {
        validarCpfDuplicado(paciente.getCpf(), null); // regra de negócio (não é validação estrutural)
        paciente.setId(sequencia.getAndIncrement());
        pacientes.put(paciente.getId(), paciente);
        return paciente;
    }

    public synchronized Paciente atualizar(Long id, Paciente novo) {
        Paciente existente = consultar(id);
        validarCpfDuplicado(novo.getCpf(), id);
        existente.setNome(novo.getNome());
        existente.setCpf(novo.getCpf());
        existente.setDataNascimento(novo.getDataNascimento());
        existente.setEmail(novo.getEmail());
        existente.setTelefone(novo.getTelefone());
        existente.setConvenio(novo.getConvenio());
        return existente;
    }

    public synchronized Paciente consultar(Long id) {
        Paciente paciente = pacientes.get(id);
        if (paciente == null)
            throw new RecursoNaoEncontradoException("PACIENTE_NAO_ENCONTRADO", "Paciente não encontrado.");
        return paciente;
    }

    public synchronized void excluir(Long id) {
        consultar(id); // lança RecursoNaoEncontradoException se não existir
        pacientes.remove(id);
    }

    public synchronized List<Paciente> listar() { return new ArrayList<>(pacientes.values()); }

    private void validarCpfDuplicado(String cpf, Long idIgnorado) {
        boolean duplicado = pacientes.values().stream()
                .anyMatch(p -> p.getCpf().equals(cpf) && !p.getId().equals(idIgnorado));
        if (duplicado) throw new RegraNegocioException("CPF_JA_CADASTRADO", "Já existe paciente com este CPF.");
    }
}
