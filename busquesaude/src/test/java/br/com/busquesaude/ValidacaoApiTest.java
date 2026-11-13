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
class ValidacaoApiTest {

    @Autowired MockMvc mvc;

    private static String paciente(String nome, String cpf, String email) {
        return """
            {"nome":"%s","cpf":"%s","dataNascimento":"1990-05-20",
             "email":"%s","telefone":"(65) 99999-9999","convenio":"Unimed"}""".formatted(nome, cpf, email);
    }

    @Test
    void payloadValidoRetorna201() throws Exception {
        mvc.perform(post("/api/pacientes").contentType(MediaType.APPLICATION_JSON)
                .content(paciente("Maria Souza", "529.982.247-25", "maria@email.com")))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.id").exists())
           .andExpect(jsonPath("$.cpf").value("52998224725"));
    }

    @Test
    void nomeVazioRetorna400() throws Exception {
        mvc.perform(post("/api/pacientes").contentType(MediaType.APPLICATION_JSON)
                .content(paciente("", "529.982.247-25", "ana@email.com")))
           .andExpect(status().isBadRequest());
    }

    @Test
    void emailInvalidoRetorna400() throws Exception {
        mvc.perform(post("/api/pacientes").contentType(MediaType.APPLICATION_JSON)
                .content(paciente("Ana", "529.982.247-25", "ana")))
           .andExpect(status().isBadRequest());
    }

    @Test
    void cpfInvalidoRetorna400() throws Exception {
        mvc.perform(post("/api/pacientes").contentType(MediaType.APPLICATION_JSON)
                .content(paciente("Ana Souza", "111.111.111-11", "ana@email.com")))
           .andExpect(status().isBadRequest());
    }

    @Test
    void corpoVazioRetorna400() throws Exception {
        mvc.perform(post("/api/pacientes").contentType(MediaType.APPLICATION_JSON).content("{}"))
           .andExpect(status().isBadRequest());
    }

    @Test
    void checkInSemCamposObrigatoriosRetorna400() throws Exception {
        mvc.perform(post("/api/checkins").contentType(MediaType.APPLICATION_JSON).content("{}"))
           .andExpect(status().isBadRequest());
    }

    @Test
    void latitudeForaDoIntervaloRetorna400() throws Exception {
        mvc.perform(get("/api/hospitais/proximos").param("lat", "200").param("lon", "-56"))
           .andExpect(status().isBadRequest());
    }

    // Regra de negócio (Service), não validação estrutural: CPF repetido -> 400
    @Test
    void cpfDuplicadoRetorna409() throws Exception {
        String corpo = paciente("Joao Lima", "111.444.777-35", "joao@email.com");
        mvc.perform(post("/api/pacientes").contentType(MediaType.APPLICATION_JSON).content(corpo))
           .andExpect(status().isCreated());
        mvc.perform(post("/api/pacientes").contentType(MediaType.APPLICATION_JSON).content(corpo))
           .andExpect(status().isBadRequest()); // regra de negócio -> 400 (padrão da Semana 09)
    }

    @Test
    void excluirPacienteInexistenteRetorna404() throws Exception {
        mvc.perform(delete("/api/pacientes/9999")).andExpect(status().isNotFound());
    }

    @Test
    void excluirPacienteExistenteRetorna204() throws Exception {
        String corpo = paciente("Carla Dias", "168.995.350-09", "carla@email.com");
        String resposta = mvc.perform(post("/api/pacientes").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String id = resposta.replaceAll(".*\"id\":(\\d+).*", "$1");
        mvc.perform(delete("/api/pacientes/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/pacientes/" + id)).andExpect(status().isNotFound());
    }
}
