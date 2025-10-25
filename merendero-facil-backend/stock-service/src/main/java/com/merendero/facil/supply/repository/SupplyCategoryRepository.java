package com.merendero.facil.supply.repository;

import com.merendero.facil.supply.entity.SupplyCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplyCategoryRepository extends JpaRepository<SupplyCategoryEntity, Long> {
}
