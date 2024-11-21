package com.project.server.controller;

import com.project.server.entity.SensorData;
import com.project.server.service.SensorDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sensors")
public class SensorDataController {
    private final SensorDataService service;

    public SensorDataController(SensorDataService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SensorData> saveData(@RequestBody SensorData data) {
        SensorData savedData = service.saveData(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedData);
    }

    @GetMapping("/{room}")
    public ResponseEntity<List<SensorData>> getDataByRoom(@PathVariable String room) {
        List<SensorData> data = service.getDataByRoom(room);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/history/{room}")
    public ResponseEntity<List<SensorData>> getSensorHistory(@PathVariable String room,
                                                             @RequestParam LocalDateTime from,
                                                             @RequestParam LocalDateTime to) {
        List<SensorData> data = service.getDataByRoomAndTimestampBetween(room, from, to);
        return ResponseEntity.ok(data);
    }
}

