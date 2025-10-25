package com.merendero.facil.expense.entity;

import com.merendero.facil.movement.entity.EntryEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un Gasto del merendero, incluyendo monto, tipo y fecha del egreso.
 **/
@Entity
@Table(name = "expenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "merendero_id")
    private Long merenderoId;

    @Column(name = "amount")
    private BigDecimal amount;

    /**
     * Puede ser nulo, ya que el gasto no siempre es de compra de insumos.
     **/
    @OneToOne
    @JoinColumn(name = "entry_id")
    private EntryEntity entry;

    @ManyToOne
    @JoinColumn(name = "type_expense_id")
    private ExpenseTypeEntity type;

    @Column(name = "expense_date")
    private LocalDateTime expenseDate;

    @PrePersist
    public void beforePersist() {
        expenseDate = LocalDateTime.now();
    }
}
