package com.merendero.facil.expense.repository;

import com.merendero.facil.expense.entity.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    @Query("SELECT e FROM ExpenseEntity e WHERE e.entry.id IN :entryIds")
    List<ExpenseEntity> findByEntryIdIn(List<Long> entryIds);

    @Query("SELECT e FROM ExpenseEntity e WHERE e.merenderoId = :merenderoId")
    List<ExpenseEntity> findByMerenderoId(Long merenderoId);

    @Query("""
        SELECT e FROM ExpenseEntity e
        WHERE
        e.expenseDate BETWEEN :desde AND :hasta
        AND e.merenderoId = :merenderoId
        """)
    List<ExpenseEntity> findByDatesAndMerendero(@Param("desde") LocalDateTime desde,
                                                 @Param("hasta")LocalDateTime hasta,
                                                 Long merenderoId);
}