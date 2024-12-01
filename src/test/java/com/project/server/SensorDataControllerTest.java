package com.project.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.server.controller.SensorDataController;
import com.project.server.entity.SensorData;
import com.project.server.service.SensorDataService;
import com.project.server.testconfig.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest(SensorDataController.class)
@Import(TestConfig.class)
@AutoConfigureMockMvc(addFilters = false) // відключення безпекових фільтрів для тестів
public class SensorDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SensorDataService sensorDataService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSaveData() throws Exception {
        SensorData sensorData = new SensorData();
        sensorData.setSensorType("Temperature");
        sensorData.setSensorValue(22.5);
        sensorData.setTimestamp(LocalDateTime.now());

        when(sensorDataService.saveData(sensorData)).thenReturn(sensorData);

        mockMvc.perform(post("/api/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sensorData)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sensorType").value("Temperature"))
                .andExpect(jsonPath("$.sensorValue").value(22.5))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

    @Test
    public void testGetDataByRoom() throws Exception {
        String room = "Living Room";
        SensorData sensorData = new SensorData();
        sensorData.setSensorType("Temperature");
        sensorData.setSensorValue(20.0);
        sensorData.setRoom(room);
        sensorData.setTimestamp(LocalDateTime.now());

        when(sensorDataService.getDataByRoom(room)).thenReturn(List.of(sensorData));

        mockMvc.perform(get("/api/sensors/{room}", room)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].sensorType").value("Temperature"))
                .andExpect(jsonPath("$.[0].sensorValue").value(20.0))
                .andExpect(jsonPath("$.[0].room").value(room))
                .andExpect(jsonPath("$.[0].timestamp").exists())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

    @Test
    public void testGetSensorHistory() throws Exception {
        String room = "Living Room";
        LocalDateTime from = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2023, 1, 10, 23, 59);

        SensorData sensorData = new SensorData();
        sensorData.setSensorType("Temperature");
        sensorData.setSensorValue(18.0);
        sensorData.setRoom(room);
        sensorData.setTimestamp(LocalDateTime.of(2023, 1, 5, 12, 0));

        when(sensorDataService.getDataByRoomAndTimestampBetween(room, from, to)).thenReturn(List.of(sensorData));

        mockMvc.perform(get("/api/sensors/history/{room}", room)
                        .param("from", from.toString())
                        .param("to", to.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].sensorType").value("Temperature"))
                .andExpect(jsonPath("$.[0].sensorValue").value(18.0))
                .andExpect(jsonPath("$.[0].room").value(room))
                .andExpect(jsonPath("$.[0].timestamp").value(sensorData.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }
}