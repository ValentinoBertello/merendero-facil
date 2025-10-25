package com.merendero.facil.supply.repository;

import com.merendero.facil.supply.entity.SupplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplyRepository extends JpaRepository<SupplyEntity, Long> {

    /** Traemos todos los insumos asociados a un merendero **/
    List<SupplyEntity> findByMerenderoIdAndActiveTrue(Long merenderoId);
}
