package in.aditya.moneymanager.controller;

import in.aditya.moneymanager.dto.IncomeDTO;
import in.aditya.moneymanager.entity.ProfileEntity;
import in.aditya.moneymanager.service.EmailService;
import in.aditya.moneymanager.service.ExcelService;
import in.aditya.moneymanager.service.ExpenseService;
import in.aditya.moneymanager.service.IncomeService;
import in.aditya.moneymanager.service.ProfileService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/email")
@RestController
public class EmailController {

    private final ExcelService excelService;
    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final EmailService emailService;
    private final ProfileService profileService;

    @GetMapping("/income-excel")
    public ResponseEntity<Void> emailIncomeExcel() throws IOException, MessagingException {
        ProfileEntity profile = profileService.getCurrentProfile();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        excelService.writeIncomesToExcel(byteArrayOutputStream, incomeService.getCurrentMonthIncomesForCurrentUser());
        emailService.sendEmailWithAttachment(profile.getEmail() , "Your Income Excel Report" , "Please find attached your income report" , byteArrayOutputStream.toByteArray() , "income.xlsx");
        return ResponseEntity.ok(null);
    }
    @GetMapping("/expense-excel")
    public ResponseEntity<Void> emailExpenseExcel() throws IOException, MessagingException {
        ProfileEntity profile = profileService.getCurrentProfile();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        excelService.writeExpensesToExcel(byteArrayOutputStream, expenseService.getCurrentMonthExpensesForCurrentUser());
        emailService.sendEmailWithAttachment(profile.getEmail() , "Your Expense Excel Report" , "Please find attached your expense report" , byteArrayOutputStream.toByteArray() , "expense.xlsx");
        return ResponseEntity.ok(null);
    }
}
