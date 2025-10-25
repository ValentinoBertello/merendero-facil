package com.merendero.facil.repository;

import com.merendero.facil.entities.MerenderoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MerenderoRepository extends JpaRepository<MerenderoEntity, Long> {
    List<MerenderoEntity> findByActiveTrue();

    Optional<MerenderoEntity> findByManagerId(Long managerId);

    Optional<MerenderoEntity> findByIdAndActiveTrue(Long id);

}
