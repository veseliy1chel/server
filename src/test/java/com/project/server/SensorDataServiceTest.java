
package com.project.server;

import com.project.server.entity.SensorData;
import com.project.server.model.AlarmEvent;
import com.project.server.repository.AlarmEventRepository;
import com.project.server.service.SensorDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SensorDataServiceTest {

    @Autowired
    private SensorDataService sensorDataService;
    @Autowired
    private AlarmEventRepository alarmEventRepository;

    @Test
    public void testSaveSensorData() {
        SensorData sensorData = new SensorData();
        sensorData.setSensorType("Temperature");
        sensorData.setSensorValue(25.0);
        SensorData savedData = sensorDataService.saveData(sensorData);
        assertNotNull(savedData);
        assertEquals("Temperature", savedData.getSensorType());
    }

    @Test
    public void testGetSensorDataByRoomAndTimeRange() {
        // Додаємо нові дані датчика
        SensorData sensorData = new SensorData();
        sensorData.setRoom("Room1");
        sensorData.setSensorType("Temperature");
        sensorData.setSensorValue(22.5);
        sensorData.setTimestamp(LocalDateTime.now().minusHours(1));
        sensorDataService.saveData(sensorData);

        // Приклад тестування запиту даних за кімнатою і часовим інтервалом
        List<SensorData> dataList = sensorDataService.getDataByRoomAndTimestampBetween("Room1", LocalDateTime.now().minusDays(1), LocalDateTime.now());
        assertNotNull(dataList);
        assertFalse(dataList.isEmpty());
    }

    @Test
    public void testSaveData() {
        SensorData sensorData = new SensorData();
        sensorData.setSensorType("Humidity");
        sensorData.setSensorValue(45.0);
        sensorData.setRoom("Room2");
        sensorData.setTimestamp(LocalDateTime.now());

        SensorData savedData = sensorDataService.saveData(sensorData);

        assertNotNull(savedData);
        assertEquals("Humidity", savedData.getSensorType());
        assertEquals(45.0, savedData.getSensorValue());
        assertEquals("Room2", savedData.getRoom());
    }

    @Test
    public void testGetDataByRoom() {
        SensorData sensorData = new SensorData();
        sensorData.setSensorType("Temperature");
        sensorData.setSensorValue(20.0);
        sensorData.setRoom("Room3");
        sensorData.setTimestamp(LocalDateTime.now());

        sensorDataService.saveData(sensorData);

        List<SensorData> sensorDataList = sensorDataService.getDataByRoom("Room3");
        assertNotNull(sensorDataList);
        assertFalse(sensorDataList.isEmpty());
        assertEquals(1, sensorDataList.size());
        assertEquals("Room3", sensorDataList.get(0).getRoom());
    }

    @Test
    public void testHandleSensorData() {
        SensorData sensorData = new SensorData();
        sensorData.setSensorType("temperature");
        sensorData.setSensorValue(35.0);
        sensorData.setRoom("Room4");
        sensorData.setTimestamp(LocalDateTime.now());

        sensorDataService.handleSensorData(sensorData);

        // Перевірка, що аварійний випадок було правильно збережено у базі даних
        List<AlarmEvent> events = alarmEventRepository.findByRoom("Room4");
        assertNotNull(events);
        assertFalse(events.isEmpty());
        assertEquals("High Temperature", events.get(0).getEventType());
    }

    @Test
    public void testGetDataByRoomAndTimestampBetween_EmptyRoom() {
        List<SensorData> sensorDataList = sensorDataService.getDataByRoomAndTimestampBetween("", LocalDateTime.now().minusDays(1), LocalDateTime.now());
        assertNotNull(sensorDataList);
        assertTrue(sensorDataList.isEmpty());
   }

   @Test
   public void testGetDataByRoomAndTimestampBetween_NullRoom() {
       List<SensorData> sensorDataList = sensorDataService.getDataByRoomAndTimestampBetween(null, LocalDateTime.now().minusDays(1), LocalDateTime.now());
       assertNotNull(sensorDataList);
       assertTrue(sensorDataList.isEmpty());
   }
}

