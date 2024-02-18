package com.example.weranga.appointmentscheduler.dao;

import com.example.weranga.appointmentscheduler.entity.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String roleName);
}
