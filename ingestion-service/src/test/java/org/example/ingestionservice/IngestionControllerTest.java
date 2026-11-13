package org.example.ingestionservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.ingestionservice.controller.ingestionController;
import org.example.ingestionservice.dto.energyUsageDto;
import org.example.ingestionservice.service.ingestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class IngestionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ingestionService ingestionService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ingestionController(ingestionService)).build();
    }

    @Test
    void shouldIngestEnergyUsageSuccessfully() throws Exception {
        energyUsageDto dto = new energyUsageDto(UUID.randomUUID(), "2026-10-29T18:00:00Z", 42.5);
        when(ingestionService.sendEnergyUsage(any(energyUsageDto.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(post("/ingestion/energy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.meterId").value(dto.meterId().toString()));

        verify(ingestionService).sendEnergyUsage(dto);
    }
}
