package com.example.salonbeauty.service;

import com.example.salonbeauty.model.ServiceEntity;
import java.util.List;

public interface SalonService {
    List<ServiceEntity> listAll();
    ServiceEntity getById(Long id);
}
