package com.example.weranga.appointmentscheduler.dao;

import com.example.weranga.appointmentscheduler.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

}
