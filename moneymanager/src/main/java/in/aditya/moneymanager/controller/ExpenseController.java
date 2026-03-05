package in.aditya.moneymanager.controller;

import in.aditya.moneymanager.dto.ExpenseDTO;
import in.aditya.moneymanager.service.ExpenseService;
import in.aditya.moneymanager.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/expenses")
@RequiredArgsConstructor
@RestController
public class ExpenseController {

    private final ExpenseService expenseService;
    private final ProfileService profileService;

    @PostMapping
    public ResponseEntity<ExpenseDTO> addExpense(@RequestBody ExpenseDTO expenseDTO){
        ExpenseDTO expense = expenseService.addExpense(expenseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(expense);
    }


    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getExpenses(){
        List<ExpenseDTO> currentMonthExpensesForCurrentUser = expenseService.getCurrentMonthExpensesForCurrentUser();
        return ResponseEntity.ok(currentMonthExpensesForCurrentUser);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long expenseId){
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.ok().build();
    }


}
