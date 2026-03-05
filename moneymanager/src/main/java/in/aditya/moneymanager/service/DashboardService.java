package in.aditya.moneymanager.service;

import in.aditya.moneymanager.dto.ExpenseDTO;
import in.aditya.moneymanager.dto.IncomeDTO;
import in.aditya.moneymanager.dto.RecentTransactionDTO;
import in.aditya.moneymanager.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Stream.concat;

@RequiredArgsConstructor
@Service
public class DashboardService {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ProfileService profileService;

    public Map<String, Object> getDashboardData() {
        ProfileEntity profile = profileService.getCurrentProfile();
        Map<String, Object> returnValue = new LinkedHashMap<>();
        List<IncomeDTO> incomes = incomeService.getLatest5IncomesForCurrentUser();
        List<ExpenseDTO> expenses = expenseService.getLatest5ExpensesForCurrentUser();
        List<RecentTransactionDTO> recentTransactions = concat(incomes.stream().map(income ->
                        RecentTransactionDTO.builder()
                                .id(income.getId())
                                .profileId(income.getProfile_id())
                                .name(income.getName())
                                .icon(income.getIcon())
                                .date(income.getDate())
                                .amount(income.getAmount())
                                .createdAt(income.getCreatedAt())
                                .updatedAt(income.getUpdatedAt())
                                .type("income")
                                .build()),
                expenses.stream().map(expense -> RecentTransactionDTO.builder()
                        .id(expense.getId())
                        .profileId(expense.getProfile_id())
                        .name(expense.getName())
                        .icon(expense.getIcon())
                        .date(expense.getDate())
                        .amount(expense.getAmount())
                        .createdAt(expense.getCreatedAt())
                        .updatedAt(expense.getUpdatedAt())
                        .type("expense")
                        .build()))
                .sorted((a, b) -> {
                    int cmp = a.getDate().compareTo(b.getDate());
                    if (cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null) {
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    }
                    return cmp;
                }).toList();
        returnValue.put("totalBalance", incomeService.getTotalIncomesForCurrentUser().subtract(expenseService.getTotalExpenseForCurrentUser()));
        returnValue.put("totalIncome", incomeService.getTotalIncomesForCurrentUser());
        returnValue.put("totalExpense", expenseService.getTotalExpenseForCurrentUser());
        returnValue.put("recent5Incomes", incomes);
        returnValue.put("recent5Expenses",expenses);
        returnValue.put("recentTransactions", recentTransactions);
        return returnValue;
    }

}
