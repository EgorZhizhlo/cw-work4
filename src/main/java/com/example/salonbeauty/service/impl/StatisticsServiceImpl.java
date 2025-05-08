package com.example.salonbeauty.service.impl;

import com.example.salonbeauty.repository.*;
import com.example.salonbeauty.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final UserRepository userRepo;
    private final ServiceEntityRepository serviceRepo;

    @Override
    public long countClients() {
        return userRepo.countByRoleName("CLIENT");
    }

    @Override
    public long countMasters() {
        return userRepo.countByRoleName("MASTER");
    }

    @Override
    public long countServices() {
        return serviceRepo.countBy();
    }
}
