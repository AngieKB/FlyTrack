package co.uniquindio.software.devops.controller;

import co.uniquindio.software.devops.model.dto.GateDTO;
import co.uniquindio.software.devops.service.GateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GateService gateService;

    @Autowired
    private ObjectMapper objectMapper;

    private GateDTO gateDTO;

    @BeforeEach
    void setUp() {
        gateDTO = GateDTO.builder()
                .id(1L)
                .gateCode("A1")
                .terminal("Terminal 1")
                .available(true)
                .build();
    }

    @Test
    @WithMockUser
    void findAll_ShouldReturnList() throws Exception {
        List<GateDTO> gates = Arrays.asList(gateDTO);
        when(gateService.findAll()).thenReturn(gates);

        mockMvc.perform(get("/api/gates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].gateCode").value("A1"));
    }

    @Test
    @WithMockUser
    void findById_ShouldReturnGate() throws Exception {
        when(gateService.findById(1L)).thenReturn(gateDTO);

        mockMvc.perform(get("/api/gates/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gateCode").value("A1"));
    }

    @Test
    @WithMockUser
    void findAvailable_ShouldReturnList() throws Exception {
        List<GateDTO> gates = Arrays.asList(gateDTO);
        when(gateService.findAvailable()).thenReturn(gates);

        mockMvc.perform(get("/api/gates/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser
    void save_ShouldReturnCreated() throws Exception {
        when(gateService.save(any(GateDTO.class))).thenReturn(gateDTO);

        mockMvc.perform(post("/api/gates")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gateCode").value("A1"));
    }

    @Test
    @WithMockUser
    void update_ShouldReturnOk() throws Exception {
        when(gateService.update(eq(1L), any(GateDTO.class))).thenReturn(gateDTO);

        mockMvc.perform(put("/api/gates/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gateCode").value("A1"));
    }

    @Test
    @WithMockUser
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(gateService).delete(1L);

        mockMvc.perform(delete("/api/gates/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
