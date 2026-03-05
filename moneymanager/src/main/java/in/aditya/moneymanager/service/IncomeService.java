package in.aditya.moneymanager.service;

import in.aditya.moneymanager.dto.ExpenseDTO;
import in.aditya.moneymanager.dto.IncomeDTO;
import in.aditya.moneymanager.entity.CategoryEntity;
import in.aditya.moneymanager.entity.ExpenseEntity;
import in.aditya.moneymanager.entity.IncomeEntity;
import in.aditya.moneymanager.entity.ProfileEntity;
import in.aditya.moneymanager.repository.CategoryRepository;
import in.aditya.moneymanager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class IncomeService {

    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;
    private final IncomeRepository incomeRepository;



    public IncomeDTO addIncome(IncomeDTO incomeDTO){
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(incomeDTO.getCategory_id()).orElseThrow(RuntimeException::new);

        IncomeEntity incomeEntity = incomeRepository.save(toEntity(incomeDTO, profile, category));
        return toDTO(incomeEntity);

    }

    //Retrieve all incomes for Current month/ based on start date and end date
    public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> incomeEntities = incomeRepository.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
        return incomeEntities.stream().map(this::toDTO).toList();
    }

    public void deleteIncome(Long incomeId){
        IncomeEntity income = incomeRepository.findById(incomeId).orElseThrow(()->new RuntimeException("Can't Find income"));
        ProfileEntity currentProfile = profileService.getCurrentProfile();
        if (!income.getProfile().getId().equals(currentProfile.getId())){
            throw new RuntimeException("Unauthorized to delete this income");
        }
        incomeRepository.delete(income);
    }

    //Get 5 latest incomes for current user
    public List<IncomeDTO> getLatest5IncomesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> incomeEntities = incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return incomeEntities.stream().map(this::toDTO).toList();
    }

    // Get Total incomes for current user
    public BigDecimal getTotalIncomesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = incomeRepository.findTotalIncomeByProfileId(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    //filter expenses
    public List<IncomeDTO> filterIncomes(LocalDate startDate , LocalDate endDate , String keyword , Sort sort){

        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> incomes = incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);
        return incomes.stream().map(this::toDTO).toList();
    }


    private IncomeEntity toEntity(IncomeDTO incomeDTO , ProfileEntity profileEntity , CategoryEntity categoryEntity){
        return IncomeEntity.builder()
                .name(incomeDTO.getName())
                .icon(incomeDTO.getIcon())
                .amount(incomeDTO.getAmount())
                .date(incomeDTO.getDate())
                .category(categoryEntity)
                .profile(profileEntity)
                .build();
    }

    private IncomeDTO toDTO(IncomeEntity incomeEntity){
        return IncomeDTO.builder()
                .id(incomeEntity.getId())
                .name(incomeEntity.getName())
                .profile_id(incomeEntity.getProfile().getId())
                .category_id(incomeEntity.getCategory().getId())
                .icon(incomeEntity.getIcon())
                .amount(incomeEntity.getAmount())
                .date(incomeEntity.getDate())
                .createdAt(incomeEntity.getCreatedAt())
                .updatedAt(incomeEntity.getUpdatedAt())
                .categoryName(incomeEntity.getCategory().getName())
                .build();
    }

}
