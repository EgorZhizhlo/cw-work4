package com.example.salonbeauty.repository;

import com.example.salonbeauty.model.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceEntityRepository extends JpaRepository<ServiceEntity, Long> {
    // Для статистики
    long countBy();

    // (Опционально) все услуги данного мастера
    List<ServiceEntity> findAllByMasterId(Long masterId);

    void deleteByMaster_Id(Long id);
}
