package com.example.salonbeauty.service;

import com.example.salonbeauty.model.Appointment;
import com.example.salonbeauty.model.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookingService {
    List<LocalTime> getAvailableSlots(Long serviceId, User master, LocalDate date);
    Appointment book(Long clientId, Long masterId, Long serviceId,
                     LocalDate date, LocalTime startTime);
    void cancelBooking(Long appointmentId, String username);
    List<Appointment> listClientAppointments(Long clientId);
    List<Appointment> listMasterAppointments(Long masterId);
    /** now returns Users with role=MASTER */
    List<User> listAllMasters();
    User getMasterById(Long masterId);
    User getMasterByUser(User user);
}
