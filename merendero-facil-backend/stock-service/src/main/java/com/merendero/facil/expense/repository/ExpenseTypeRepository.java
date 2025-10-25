package com.merendero.facil.expense.repository;

import com.merendero.facil.expense.entity.ExpenseTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseTypeRepository extends JpaRepository<ExpenseTypeEntity, Long> {
}
