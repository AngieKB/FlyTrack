package co.uniquindio.software.devops.controller;

import co.uniquindio.software.devops.model.dto.NotificationDTO;
import co.uniquindio.software.devops.model.entity.NotificationType;
import co.uniquindio.software.devops.service.NotificationService;
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
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private NotificationDTO notificationDTO;

    @BeforeEach
    void setUp() {
        notificationDTO = NotificationDTO.builder()
                .id(1L)
                .message("Vuelo retrasado")
                .type(NotificationType.DELAY)
                .sentAt(LocalDateTime.now())
                .read(false)
                .flightId(1L)
                .build();
    }

    @Test
    @WithMockUser
    void findAll_ShouldReturnList() throws Exception {
        List<NotificationDTO> notifications = Arrays.asList(notificationDTO);
        when(notificationService.findAll()).thenReturn(notifications);

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].message").value("Vuelo retrasado"));
    }

    @Test
    @WithMockUser
    void findById_ShouldReturnNotification() throws Exception {
        when(notificationService.findById(1L)).thenReturn(notificationDTO);

        mockMvc.perform(get("/api/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Vuelo retrasado"));
    }

    @Test
    @WithMockUser
    void findByFlight_ShouldReturnList() throws Exception {
        List<NotificationDTO> notifications = Arrays.asList(notificationDTO);
        when(notificationService.findByFlightId(1L)).thenReturn(notifications);

        mockMvc.perform(get("/api/notifications/flight/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser
    void save_ShouldReturnCreated() throws Exception {
        when(notificationService.save(any(NotificationDTO.class))).thenReturn(notificationDTO);

        mockMvc.perform(post("/api/notifications")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Vuelo retrasado"));
    }

    @Test
    @WithMockUser
    void markAsRead_ShouldReturnOk() throws Exception {
        notificationDTO.setRead(true);
        when(notificationService.markAsRead(1L)).thenReturn(notificationDTO);

        mockMvc.perform(patch("/api/notifications/1/read")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.read").value(true));
    }

    @Test
    @WithMockUser
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(notificationService).delete(1L);

        mockMvc.perform(delete("/api/notifications/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
