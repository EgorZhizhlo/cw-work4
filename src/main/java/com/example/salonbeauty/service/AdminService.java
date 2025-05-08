package com.example.salonbeauty.service;

import com.example.salonbeauty.model.*;

import java.util.List;

public interface AdminService {
    // Пользователи
    List<User> listUsers();
    User   getUser(Long id);
    User   updateUser(User u);
    void   deleteUser(Long id);

    // Услуги
    List<ServiceEntity> listServices();
    ServiceEntity getService(Long id);
    ServiceEntity saveService(ServiceEntity s);
    void deleteService(Long id);

    // Записи
    List<Appointment> listAppointments();
    Appointment getAppointment(Long id);
    void updateAppointmentStatus(Long id, AppointmentStatus status);
    void deleteAppointment(Long id);

    long countUsers();
    long countServices();
    long countAllAppointments();
    long countAppointmentsByStatus(AppointmentStatus status);

    List<Role> listRoles();
    List<User> listMasters();


}
