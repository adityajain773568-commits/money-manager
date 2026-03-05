package in.aditya.moneymanager.service;

import in.aditya.moneymanager.dto.CategoryDTO;
import in.aditya.moneymanager.dto.ExpenseDTO;
import in.aditya.moneymanager.entity.CategoryEntity;
import in.aditya.moneymanager.entity.ExpenseEntity;
import in.aditya.moneymanager.entity.ProfileEntity;
import in.aditya.moneymanager.repository.CategoryRepository;
import in.aditya.moneymanager.repository.ExpenseRepository;
import in.aditya.moneymanager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ExpenseService {

    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;
    private final ExpenseRepository expenseRepository;


    public ExpenseDTO addExpense(ExpenseDTO expenseDTO){
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(expenseDTO.getCategory_id()).orElseThrow(RuntimeException::new);

        ExpenseEntity expenseEntity = expenseRepository.save(toEntity(expenseDTO, profile, category));
        return toDTO(expenseEntity);

    }

    //Retrieve all expenses for Current month/ based on start date and end date
    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<ExpenseEntity> expenseEntities = expenseRepository.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
        return expenseEntities.stream().map(this::toDTO).toList();
    }

    public void deleteExpense(Long expenseId){
        ExpenseEntity expense = expenseRepository.findById(expenseId).orElseThrow(()->new RuntimeException("Can't Find expense"));
        ProfileEntity currentProfile = profileService.getCurrentProfile();
        if (!expense.getProfile().getId().equals(currentProfile.getId())){
            throw new RuntimeException("Unauthorized to delete this expense");
        }
        expenseRepository.delete(expense);
    }

    //Get 5 latest expenses for current user
    public List<ExpenseDTO> getLatest5ExpensesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> expenseEntities = expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return expenseEntities.stream().map(this::toDTO).toList();
    }

    // Get Total expense for current user
    public BigDecimal getTotalExpenseForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = expenseRepository.findTotalExpenseByProfileId(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    //filter expenses
    public List<ExpenseDTO> filterExpenses(LocalDate startDate , LocalDate endDate , String keyword , Sort sort){

        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> expenses = expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);
        return expenses.stream().map(this::toDTO).toList();
    }

    //find by profile id and date for notifications
    public List<ExpenseDTO> getExpensesForUserOnDate(Long profileId,LocalDate date){
        List<ExpenseEntity> expenses = expenseRepository.findByProfileIdAndDate(profileId, date);
        return expenses.stream().map(this::toDTO).toList();
    }

    private ExpenseEntity toEntity(ExpenseDTO expenseDTO , ProfileEntity profileEntity , CategoryEntity categoryEntity){
        return ExpenseEntity.builder()
                .name(expenseDTO.getName())
                .icon(expenseDTO.getIcon())
                .amount(expenseDTO.getAmount())
                .date(expenseDTO.getDate())
                .category(categoryEntity)
                .profile(profileEntity)
                .build();
    }

    private ExpenseDTO toDTO(ExpenseEntity expenseEntity){
        return ExpenseDTO.builder()
                .id(expenseEntity.getId())
                .name(expenseEntity.getName())
                .profile_id(expenseEntity.getProfile().getId())
                .category_id(expenseEntity.getCategory().getId())
                .icon(expenseEntity.getIcon())
                .amount(expenseEntity.getAmount())
                .date(expenseEntity.getDate())
                .createdAt(expenseEntity.getCreatedAt())
                .updatedAt(expenseEntity.getUpdatedAt())
                .categoryName(expenseEntity.getCategory().getName())
                .build();
    }
}
