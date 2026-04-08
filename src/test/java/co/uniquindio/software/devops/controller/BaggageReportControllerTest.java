package co.uniquindio.software.devops.controller;

import co.uniquindio.software.devops.model.dto.BaggageReportDTO;
import co.uniquindio.software.devops.model.entity.BaggageStatus;
import co.uniquindio.software.devops.service.BaggageReportService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BaggageReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BaggageReportService baggageReportService;

    @Autowired
    private ObjectMapper objectMapper;

    private BaggageReportDTO baggageReportDTO;

    @BeforeEach
    void setUp() {
        baggageReportDTO = BaggageReportDTO.builder()
                .id(1L)
                .description("Maleta perdida")
                .status(BaggageStatus.REPORTED)
                .reportedAt(LocalDateTime.now())
                .baggageTag("TAG123")
                .passengerId(1L)
                .build();
    }

    @Test
    @WithMockUser
    void findAll_ShouldReturnList() throws Exception {
        List<BaggageReportDTO> reports = Arrays.asList(baggageReportDTO);
        when(baggageReportService.findAll()).thenReturn(reports);

        mockMvc.perform(get("/api/baggage-reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].description").value("Maleta perdida"));
    }

    @Test
    @WithMockUser
    void findById_ShouldReturnReport() throws Exception {
        when(baggageReportService.findById(1L)).thenReturn(baggageReportDTO);

        mockMvc.perform(get("/api/baggage-reports/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Maleta perdida"));
    }

    @Test
    @WithMockUser
    void findByPassenger_ShouldReturnList() throws Exception {
        List<BaggageReportDTO> reports = Arrays.asList(baggageReportDTO);
        when(baggageReportService.findByPassengerId(1L)).thenReturn(reports);

        mockMvc.perform(get("/api/baggage-reports/passenger/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser
    void save_ShouldReturnCreated() throws Exception {
        when(baggageReportService.save(any(BaggageReportDTO.class))).thenReturn(baggageReportDTO);

        mockMvc.perform(post("/api/baggage-reports")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baggageReportDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Maleta perdida"));
    }

    @Test
    @WithMockUser
    void updateStatus_ShouldReturnOk() throws Exception {
        baggageReportDTO.setStatus(BaggageStatus.IN_REVIEW);
        when(baggageReportService.updateStatus(1L, BaggageStatus.IN_REVIEW)).thenReturn(baggageReportDTO);

        mockMvc.perform(patch("/api/baggage-reports/1/status/IN_REVIEW")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_REVIEW"));
    }

    @Test
    @WithMockUser
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(baggageReportService).delete(1L);

        mockMvc.perform(delete("/api/baggage-reports/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
