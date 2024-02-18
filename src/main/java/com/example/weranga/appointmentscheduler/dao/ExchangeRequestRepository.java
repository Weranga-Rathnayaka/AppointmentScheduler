package com.example.weranga.appointmentscheduler.dao;

import com.example.weranga.appointmentscheduler.entity.ExchangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Integer> {
}
