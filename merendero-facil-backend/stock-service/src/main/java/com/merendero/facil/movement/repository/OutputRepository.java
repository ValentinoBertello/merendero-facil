package com.merendero.facil.movement.repository;

import com.merendero.facil.movement.entity.OutputEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OutputRepository extends JpaRepository<OutputEntity,Long> {

    @Query("SELECT o FROM OutputEntity o WHERE o.supply.merenderoId = :merenderoId")
    List<OutputEntity> findByMerenderoId(Long merenderoId);

    @Query("""
        SELECT o FROM OutputEntity o
        WHERE
        o.outputDate BETWEEN :desde AND :hasta
        AND o.supply.id = :supplyId
        """)
    List<OutputEntity> findByDatesAndSupply(
            @Param("desde") LocalDateTime desde,
            @Param("hasta")LocalDateTime hasta,
            @Param("supplyId") Long supplyId
    );
}