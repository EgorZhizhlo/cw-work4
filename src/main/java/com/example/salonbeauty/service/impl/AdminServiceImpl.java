package com.example.salonbeauty.service.impl;

import com.example.salonbeauty.model.*;
import com.example.salonbeauty.repository.*;
import com.example.salonbeauty.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final ServiceEntityRepository serviceRepo;
    private final AppointmentRepository apptRepo;

    @Override
    public List<User> listUsers() {
        return userRepo.findAll();
    }

    @Override
    public User getUser(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));
    }

    @Override
    public List<Role> listRoles() {
        return roleRepo.findAll();
    }

    @Override
    @Transactional
    public User updateUser(User u) {
        return userRepo.save(u);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        apptRepo.deleteByMaster_Id(id);
        apptRepo.deleteByClient_Id(id);
        serviceRepo.deleteByMaster_Id(id);
        userRepo.deleteById(id);
    }

    @Override
    public List<ServiceEntity> listServices() {
        return serviceRepo.findAll();
    }

    @Override
    public ServiceEntity getService(Long id) {
        return serviceRepo.findById(id).orElseThrow();
    }

    @Override
    @Transactional
    public ServiceEntity saveService(ServiceEntity s) {
        return serviceRepo.save(s);
    }

    @Override
    @Transactional
    public void deleteService(Long id) {
        serviceRepo.deleteById(id);
    }

    @Override
    public List<Appointment> listAppointments() {
        return apptRepo.findAll();
    }

    @Override
    public Appointment getAppointment(Long id) {
        return apptRepo.findById(id).orElseThrow();
    }

    @Override
    @Transactional
    public void updateAppointmentStatus(Long id, AppointmentStatus status) {
        Appointment a = getAppointment(id);
        a.setStatus(status);
        apptRepo.save(a);
    }

    @Override
    @Transactional
    public void deleteAppointment(Long id) {
        apptRepo.deleteById(id);
    }

    @Override
    public long countUsers() {
        return userRepo.count();
    }

    @Override
    public long countServices() {
        return serviceRepo.count();
    }

    @Override
    public long countAllAppointments() {
        return apptRepo.count();
    }

    @Override
    public long countAppointmentsByStatus(AppointmentStatus status) {
        return apptRepo.countByStatus(status);
    }

    @Override
    public List<User> listMasters() {
        return userRepo.findAllByRole_Name("MASTER");
    }

}
