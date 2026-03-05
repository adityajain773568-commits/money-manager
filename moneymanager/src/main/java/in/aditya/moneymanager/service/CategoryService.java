package in.aditya.moneymanager.service;

import in.aditya.moneymanager.dto.CategoryDTO;
import in.aditya.moneymanager.entity.CategoryEntity;
import in.aditya.moneymanager.entity.ProfileEntity;
import in.aditya.moneymanager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;


    public CategoryDTO saveCategory(CategoryDTO categoryDTO){
        ProfileEntity profile = profileService.getCurrentProfile();
        if (categoryRepository.existsByNameAndProfileId(categoryDTO.getName(), profile.getId())){
            throw new RuntimeException("Category with this name already exists");
        }
        CategoryEntity newCategory = toEntity(categoryDTO, profile);
        newCategory  = categoryRepository.save(newCategory);
        return toDTO(newCategory);
    }


    public List<CategoryDTO> getCategoriesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categories = categoryRepository.findByProfileId(profile.getId());
        return categories.stream().map(this::toDTO).toList();
    }


    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type){
        ProfileEntity currentProfile = profileService.getCurrentProfile();
        List<CategoryEntity> categoryByType = categoryRepository.findByTypeAndProfileId(type, currentProfile.getId());
        return categoryByType.stream().map(this::toDTO).toList();
    }

    public CategoryDTO updateCategory(Long categoryId,CategoryDTO categoryDTO){
        ProfileEntity currentProfile = profileService.getCurrentProfile();
        CategoryEntity existingCategory = categoryRepository.findByIdAndProfileId(categoryId, currentProfile.getId()).orElseThrow(() -> new RuntimeException("Category Not Found or Not Accessible"));
        existingCategory.setName(categoryDTO.getName());
        existingCategory.setIcon(categoryDTO.getIcon());
        existingCategory.setType(categoryDTO.getType());
        return toDTO(categoryRepository.save(existingCategory));
    }


    private CategoryEntity toEntity(CategoryDTO categoryDTO , ProfileEntity profile){
        return CategoryEntity.builder()
                .name(categoryDTO.getName())
                .type(categoryDTO.getType())
                .icon(categoryDTO.getIcon())
                .profile(profile)
                .build();
    }

    private CategoryDTO toDTO(CategoryEntity categoryEntity){
        return CategoryDTO.builder()
                .id(categoryEntity.getId())
                .profileId(categoryEntity.getProfile().getId())
                .name(categoryEntity.getName())
                .icon(categoryEntity.getIcon())
                .createdAt(categoryEntity.getCreatedAt())
                .updatedAt(categoryEntity.getUpdatedAt())
                .type(categoryEntity.getType())
                .build();
    }
}
