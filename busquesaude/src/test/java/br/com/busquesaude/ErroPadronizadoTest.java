package br.com.busquesaude;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ErroPadronizadoTest {

    @Autowired MockMvc mvc;

    @Test
    void pacienteInexistenteRetorna404Padronizado() throws Exception {
        mvc.perform(get("/api/pacientes/99999"))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.status").value(404))
           .andExpect(jsonPath("$.erro").value("Recurso não encontrado"))
           .andExpect(jsonPath("$.mensagem").value("Paciente não encontrado."))
           .andExpect(jsonPath("$.path").value("/api/pacientes/99999"))
           .andExpect(jsonPath("$.codigo").value("PACIENTE_NAO_ENCONTRADO"))
           .andExpect(jsonPath("$.campos").isEmpty());
    }

    @Test
    void dtoInvalidoRetorna400ComListaDeCampos() throws Exception {
        mvc.perform(post("/api/pacientes").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"\",\"email\":\"x\"}"))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.erro").value("Erro de validação"))
           .andExpect(jsonPath("$.campos").isNotEmpty())
           .andExpect(jsonPath("$.campos[?(@.campo=='email')].mensagem").value("Informe um e-mail válido."));
    }

    @Test
    void parametroIncompativelRetorna400() throws Exception {
        mvc.perform(get("/api/pacientes/abc"))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.erro").value("Parâmetro inválido"));
    }

    @Test
    void enumInvalidoRetorna400ComMensagemControlada() throws Exception {
        mvc.perform(post("/api/checkins").contentType(MediaType.APPLICATION_JSON)
                .content("{\"pacienteId\":1,\"hospitalId\":1,\"queixaPrincipal\":\"Dor no peito\",\"nivelUrgencia\":\"DESCONHECIDA\"}"))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.erro").value("JSON inválido"))
           .andExpect(jsonPath("$.mensagem").value("Não foi possível interpretar os dados enviados."));
    }

    @Test
    void jsonMalformadoRetorna400() throws Exception {
        mvc.perform(post("/api/pacientes").contentType(MediaType.APPLICATION_JSON).content("{nome:"))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.erro").value("JSON inválido"));
    }

    @Test
    void regraDeNegocioRetorna400Padronizado() throws Exception {
        String corpo = """
            {"nome":"Rita Alves","cpf":"529.982.247-25","dataNascimento":"1991-02-02",
             "email":"rita@email.com","telefone":"(65) 99999-9999"}""";
        // 1º cadastro pode já existir em outro teste (contexto compartilhado): ignoramos o status.
        mvc.perform(post("/api/pacientes").contentType(MediaType.APPLICATION_JSON).content(corpo));
        mvc.perform(post("/api/pacientes").contentType(MediaType.APPLICATION_JSON).content(corpo))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.erro").value("Regra de negócio violada"))
           .andExpect(jsonPath("$.codigo").value("CPF_JA_CADASTRADO"));
    }

    @Test
    void latitudeInvalidaRetorna400ComCampos() throws Exception {
        mvc.perform(get("/api/hospitais/proximos").param("lat", "200").param("lon", "-56"))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.campos").isNotEmpty());
    }

    @Test
    void rotaInexistenteRetorna404() throws Exception {
        mvc.perform(get("/api/nao-existe")).andExpect(status().isNotFound());
    }
}
