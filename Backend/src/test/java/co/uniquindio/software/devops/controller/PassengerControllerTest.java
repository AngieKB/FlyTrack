package co.uniquindio.software.devops.controller;

import co.uniquindio.software.devops.model.dto.PassengerDTO;
import co.uniquindio.software.devops.service.PassengerService;
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
public class PassengerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PassengerService passengerService;

    @Autowired
    private ObjectMapper objectMapper;

    private PassengerDTO passengerDTO;

    @BeforeEach
    void setUp() {
        passengerDTO = PassengerDTO.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Perez")
                .email("juan.perez@example.com")
                .documentNumber("123456789")
                .seatNumber("12A")
                .flightId(1L)
                .build();
    }

    @Test
    @WithMockUser
    void findAll_ShouldReturnList() throws Exception {
        List<PassengerDTO> passengers = Arrays.asList(passengerDTO);
        when(passengerService.findAll()).thenReturn(passengers);

        mockMvc.perform(get("/api/passengers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Juan"));
    }

    @Test
    @WithMockUser
    void findById_ShouldReturnPassenger() throws Exception {
        when(passengerService.findById(1L)).thenReturn(passengerDTO);

        mockMvc.perform(get("/api/passengers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Juan"));
    }

    @Test
    @WithMockUser
    void findByFlight_ShouldReturnList() throws Exception {
        List<PassengerDTO> passengers = Arrays.asList(passengerDTO);
        when(passengerService.findByFlightId(1L)).thenReturn(passengers);

        mockMvc.perform(get("/api/passengers/flight/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser
    void save_ShouldReturnCreated() throws Exception {
        when(passengerService.save(any(PassengerDTO.class))).thenReturn(passengerDTO);

        mockMvc.perform(post("/api/passengers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Juan"));
    }

    @Test
    @WithMockUser
    void update_ShouldReturnOk() throws Exception {
        when(passengerService.update(eq(1L), any(PassengerDTO.class))).thenReturn(passengerDTO);

        mockMvc.perform(put("/api/passengers/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Juan"));
    }

    @Test
    @WithMockUser
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(passengerService).delete(1L);

        mockMvc.perform(delete("/api/passengers/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
