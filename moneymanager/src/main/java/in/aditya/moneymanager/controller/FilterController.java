package in.aditya.moneymanager.controller;

import in.aditya.moneymanager.dto.ExpenseDTO;
import in.aditya.moneymanager.dto.FilterDTO;
import in.aditya.moneymanager.dto.IncomeDTO;
import in.aditya.moneymanager.service.ExpenseService;
import in.aditya.moneymanager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/filter")
@RequiredArgsConstructor
@RestController
public class FilterController {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDTO filter){
        LocalDate startDate = filter.getStartDate()==null ? LocalDate.MIN : filter.getStartDate();
        LocalDate endDate = filter.getEndDate() != null ? filter.getEndDate() : LocalDate.now();
        String keyword = filter.getKeyword() != null ? filter.getKeyword() : "";
        String sortField = filter.getSortField()!=null ? filter.getSortField() :  "date";
        Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortOrder())? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortField);
        if ("income".equalsIgnoreCase(filter.getType())){
            List<IncomeDTO> filterIncomes = incomeService.filterIncomes(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(filterIncomes);
        }else if ("expense".equalsIgnoreCase(filter.getType())){
            List<ExpenseDTO> filterExpenses = expenseService.filterExpenses(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(filterExpenses);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
