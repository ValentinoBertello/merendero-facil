package com.merendero.facil.movement.repository;

import com.merendero.facil.movement.entity.EntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EntryRepository extends JpaRepository<EntryEntity, Long> {

    @Query("SELECT e FROM EntryEntity e WHERE e.supply.merenderoId = :merenderoId")
    List<EntryEntity> findByMerenderoId(Long merenderoId);

    @Query("""
        SELECT e FROM EntryEntity e
        WHERE
        e.entryDate BETWEEN :desde AND :hasta
        AND e.supply.id = :supplyId
        """)
    List<EntryEntity> findByDatesAndSupply(
            @Param("desde") LocalDateTime desde,
            @Param("hasta")LocalDateTime hasta,
            @Param("supplyId") Long supplyId
    );
}
