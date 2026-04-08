package co.uniquindio.software.devops.controller;

import co.uniquindio.software.devops.model.dto.FlightDTO;
import co.uniquindio.software.devops.model.entity.FlightStatus;
import co.uniquindio.software.devops.service.FlightService;
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

import java.time.LocalDateTime;
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
public class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightService flightService;

    @Autowired
    private ObjectMapper objectMapper;

    private FlightDTO flightDTO;

    @BeforeEach
    void setUp() {
        flightDTO = FlightDTO.builder()
                .id(1L)
                .flightNumber("AV123")
                .origin("BOG")
                .destination("AXM")
                .departureTime(LocalDateTime.now().plusHours(2))
                .arrivalTime(LocalDateTime.now().plusHours(3))
                .status(FlightStatus.SCHEDULED)
                .airline("Avianca")
                .build();
    }

    @Test
    @WithMockUser
    void findAll_ShouldReturnList() throws Exception {
        List<FlightDTO> flights = Arrays.asList(flightDTO);
        when(flightService.findAll()).thenReturn(flights);

        mockMvc.perform(get("/api/flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].flightNumber").value("AV123"));
    }

    @Test
    @WithMockUser
    void findById_ShouldReturnFlight() throws Exception {
        when(flightService.findById(1L)).thenReturn(flightDTO);

        mockMvc.perform(get("/api/flights/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber").value("AV123"));
    }

    @Test
    @WithMockUser
    void findByFlightNumber_ShouldReturnFlight() throws Exception {
        when(flightService.findByFlightNumber("AV123")).thenReturn(flightDTO);

        mockMvc.perform(get("/api/flights/number/AV123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber").value("AV123"));
    }

    @Test
    @WithMockUser
    void findByStatus_ShouldReturnList() throws Exception {
        List<FlightDTO> flights = Arrays.asList(flightDTO);
        when(flightService.findByStatus(FlightStatus.SCHEDULED)).thenReturn(flights);

        mockMvc.perform(get("/api/flights/status/SCHEDULED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser
    void save_ShouldReturnCreated() throws Exception {
        when(flightService.save(any(FlightDTO.class))).thenReturn(flightDTO);

        mockMvc.perform(post("/api/flights")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(flightDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flightNumber").value("AV123"));
    }

    @Test
    @WithMockUser
    void update_ShouldReturnOk() throws Exception {
        when(flightService.update(eq(1L), any(FlightDTO.class))).thenReturn(flightDTO);

        mockMvc.perform(put("/api/flights/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(flightDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber").value("AV123"));
    }

    @Test
    @WithMockUser
    void updateStatus_ShouldReturnOk() throws Exception {
        flightDTO.setStatus(FlightStatus.DELAYED);
        when(flightService.updateStatus(1L, FlightStatus.DELAYED)).thenReturn(flightDTO);

        mockMvc.perform(patch("/api/flights/1/status/DELAYED")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELAYED"));
    }

    @Test
    @WithMockUser
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(flightService.delete(1L));

        mockMvc.perform(delete("/api/flights/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
