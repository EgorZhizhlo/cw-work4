// src/main/java/com/example/salonbeauty/repository/AppointmentRepository.java
package com.example.salonbeauty.repository;

import com.example.salonbeauty.model.Appointment;
import com.example.salonbeauty.model.AppointmentStatus;
import com.example.salonbeauty.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByMasterAndDate(User master, LocalDate date);

    // добaвьте
    Optional<Appointment> findByMasterAndDateAndStartTime(User master,
                                                          LocalDate date,
                                                          LocalTime startTime);


    boolean existsByMasterAndDateAndStartTime(User master, LocalDate date, LocalTime startTime);

    List<Appointment> findByClient(User client);

    void deleteByMaster_Id(Long id);
    void deleteByClient_Id(Long id);

    List<Appointment> findByMaster(User master);
    long countByStatus(AppointmentStatus status);

}
