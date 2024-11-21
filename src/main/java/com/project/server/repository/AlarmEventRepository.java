package com.project.server.repository;

import com.project.server.model.AlarmEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmEventRepository extends JpaRepository<AlarmEvent, Long> {
}

