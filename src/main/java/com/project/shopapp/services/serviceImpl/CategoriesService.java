package com.project.shopapp.services.serviceImpl;

import com.project.shopapp.dto.CategoriesDTO;
import com.project.shopapp.model.Categories;
import com.project.shopapp.repository.CategoriesRepository;
import com.project.shopapp.services.ICategoriesService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriesService implements ICategoriesService {

    private final CategoriesRepository categoriesRepository;

    @Override
    public Categories createCategories(CategoriesDTO categoriesDTO) {
        // Mapping từ DTO sang Model chỉ có 1 field name
        Categories categoriesModel = Categories.builder()
                .name(categoriesDTO.getName())
                .build();
        return categoriesRepository.save(categoriesModel);
    }

    @Override
    public Categories getCategoryById(Long id) {
        return categoriesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categories not found"));
    }

    @Override
    public List<Categories> getAllCategories() {
        return categoriesRepository.findAll();
    }

    @Override
    public Categories updateCategories(Long id, CategoriesDTO categoriesDTO) {
        Categories categories = Categories.builder()
                .name(categoriesDTO.getName())
                .build();
        Categories existingCategories = getCategoryById(id);
        existingCategories.setName(categories.getName());
        categoriesRepository.save(existingCategories);
        return existingCategories;
    }

    @Override
    public void deleteCategories(Long id) {
        categoriesRepository.deleteById(id);
    }
}
