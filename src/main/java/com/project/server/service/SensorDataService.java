package com.project.server.service;

import com.project.server.entity.SensorData;
import com.project.server.model.AlarmEvent;
import com.project.server.repository.AlarmEventRepository;
import com.project.server.repository.SensorDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SensorDataService {

    private final SensorDataRepository repository;
    private final AlarmEventRepository alarmEventRepository;
    private final WebSocketNotificationService notificationService;

    @Autowired
    public SensorDataService(SensorDataRepository repository, AlarmEventRepository alarmEventRepository, WebSocketNotificationService notificationService) {
        this.repository = repository;
        this.alarmEventRepository = alarmEventRepository;
        this.notificationService = notificationService;
    }

    public void handleSensorData(SensorData data) {
        if ("temperature".equals(data.getSensorType()) && data.getSensorValue() > 30) {
            // Створити аварійну ситуацію
            AlarmEvent event = new AlarmEvent();
            event.setRoom(data.getRoom());
            event.setEventType("High Temperature");
            event.setDescription("Температура перевищила допустиму межу");
            event.setTimestamp(LocalDateTime.now());
            alarmEventRepository.save(event);

            // Надіслати аварійне сповіщення через WebSocket
            notificationService.sendNotification("/topic/alerts", event);
        }
    }

    public SensorData saveData(SensorData data) {
        return repository.save(data);
    }

    public List<SensorData> getDataByRoom(String room) {
        return repository.findByRoom(room);
    }

    public List<SensorData> getDataByRoomAndTimestampBetween(String room, LocalDateTime from, LocalDateTime to) {
        List<SensorData> sensors = repository.findByRoomAndTimestampBetween(room, from, to);
        return sensors.stream().map(sensor -> {
            SensorData sensorData = new SensorData();
            sensorData.setSensorType(sensor.getSensorType());
            sensorData.setRoom(sensor.getRoom());
            sensorData.setSensorValue(sensor.getSensorValue());
            sensorData.setTimestamp(sensor.getTimestamp());
            return sensorData;
        }).toList();
    }
}
