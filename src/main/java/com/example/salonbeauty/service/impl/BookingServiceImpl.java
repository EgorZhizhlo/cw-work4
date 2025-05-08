// src/main/java/com/example/salonbeauty/service/impl/BookingServiceImpl.java
package com.example.salonbeauty.service.impl;

import com.example.salonbeauty.model.*;
import com.example.salonbeauty.repository.*;
import com.example.salonbeauty.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final AppointmentRepository apptRepo;
    private final ServiceEntityRepository serviceRepo;
    private final UserRepository userRepo;

    @Override
    public List<LocalTime> getAvailableSlots(Long serviceId, User master, LocalDate date) {
        ServiceEntity svc = serviceRepo.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        int duration = svc.getDurationMinutes();
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end   = LocalTime.of(22, 0).minusMinutes(duration);

        // Слоты, уже занятые у этого мастера на заданную дату
        Set<LocalTime> booked = apptRepo
                .findByMasterAndDate(master, date).stream()
                .filter(a -> a.getStatus() == AppointmentStatus.BOOKED)   // <‑‑ игнорируем CANCELLED
                .map(Appointment::getStartTime)
                .collect(Collectors.toSet());


        List<LocalTime> slots = new ArrayList<>();
        for (LocalTime t = start; !t.isAfter(end); t = t.plusMinutes(duration)) {
            if (!booked.contains(t)) {
                slots.add(t);
            }
        }
        return slots;
    }

    // ===== book =====
    @Override
    @Transactional
    public Appointment book(Long clientId, Long masterId, Long serviceId,
                            LocalDate date, LocalTime startTime) {

        User m = userRepo.findById(masterId)
                .filter(u -> u.getRole().getName().equals("MASTER"))
                .orElseThrow(() -> new RuntimeException("Master not found"));

        ServiceEntity svc = serviceRepo.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        User client = userRepo.findById(clientId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1) ищем запись в этот слот
        Optional<Appointment> opt = apptRepo
                .findByMasterAndDateAndStartTime(m, date, startTime);

        if (opt.isPresent()) {
            Appointment a = opt.get();

            if (a.getStatus() == AppointmentStatus.BOOKED) {
                // место занято активной записью
                throw new RuntimeException("Slot already taken");
            }

            // 2) запись была CANCELLED → «реактивируем»
            a.setClient(client);
            a.setService(svc);
            a.setStatus(AppointmentStatus.BOOKED);
            a.setEndTime(startTime.plusMinutes(svc.getDurationMinutes()));
            return apptRepo.save(a);
        }

        // 3) слота ещё нет → создаём
        Appointment a = Appointment.builder()
                .client(client)
                .master(m)
                .service(svc)
                .date(date)
                .startTime(startTime)
                .endTime(startTime.plusMinutes(svc.getDurationMinutes()))
                .status(AppointmentStatus.BOOKED)
                .build();

        return apptRepo.save(a);
    }


    @Override
    @Transactional
    public void cancelBooking(Long appointmentId, String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Appointment appt = apptRepo.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        boolean isOwner = appt.getClient().getUsername().equals(username);
        boolean isAdmin = user.getRole().getName().equals("ADMIN");

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You are not allowed to cancel this appointment");
        }

        appt.setStatus(AppointmentStatus.CANCELLED);
        apptRepo.save(appt);
    }

    @Override
    public List<Appointment> listClientAppointments(Long clientId) {
        User client = userRepo.findById(clientId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return apptRepo.findByClient(client);
    }

    @Override
    public List<Appointment> listMasterAppointments(Long masterId) {
        User master = getMasterById(masterId);
        return apptRepo.findByMaster(master);
    }

    @Override
    public List<User> listAllMasters() {
        return userRepo.findAllByRole_Name("MASTER");
    }

    @Override
    public User getMasterById(Long masterId) {
        return userRepo.findById(masterId)
                .filter(u -> u.getRole().getName().equals("MASTER"))
                .orElseThrow(() -> new RuntimeException("Master not found"));
    }

    @Override
    public User getMasterByUser(User user) {
        if (!user.getRole().getName().equals("MASTER")) {
            throw new RuntimeException("User is not a master");
        }
        return user;
    }
}
