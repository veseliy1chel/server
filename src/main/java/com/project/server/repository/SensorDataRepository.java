package com.project.server.repository;

import com.project.server.entity.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    List<SensorData> findByRoom(String room);
    List<SensorData> findByRoomAndTimestampBetween(String room, LocalDateTime from, LocalDateTime to);
}


