package com.example.salonbeauty.service.impl;

import com.example.salonbeauty.model.ServiceEntity;
import com.example.salonbeauty.repository.ServiceEntityRepository;
import com.example.salonbeauty.service.SalonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalonServiceImpl implements SalonService {
    private final ServiceEntityRepository repo;

    @Override
    public List<ServiceEntity> listAll() {
        return repo.findAll();
    }

    @Override
    public ServiceEntity getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }
}
