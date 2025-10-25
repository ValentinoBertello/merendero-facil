package com.merendero.facil.expense.service;


import com.merendero.facil.common.clients.dto.DonationDateSummary;
import com.merendero.facil.expense.dto.statistics.TimeGroupedExpenseData;
import com.merendero.facil.expense.entity.ExpenseEntity;

import java.util.List;

public interface DataGroupingService {
    List<TimeGroupedExpenseData> groupDataByPeriod(List<ExpenseEntity> expenses, List<DonationDateSummary> donations,
                                                   String groupBy);
}
