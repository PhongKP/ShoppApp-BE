package com.project.shopapp.controller;

import com.github.javafaker.Cat;
import com.project.shopapp.dto.CategoriesDTO;
import com.project.shopapp.model.Categories;
import com.project.shopapp.response.CategoryResponse;
import com.project.shopapp.services.ICategoriesService;
import com.project.shopapp.utils.LocalizationUtils;
import com.project.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/categories")
// Dependencies Injection (DI)
@RequiredArgsConstructor
public class CategoriesController {

    private final ICategoriesService categoriesService;
    private final LocalizationUtils localizationUtils;

    @GetMapping("")
    public ResponseEntity<?> getAllCategories(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ){
        List<Categories> categoriesList = categoriesService.getAllCategories();
        return ResponseEntity.ok(
                CategoryResponse.builder()
                        .categoriesList(categoriesList)
                        .build()
        );
    }

    @PostMapping("")
    public ResponseEntity<?> createCategories(
            @Valid @RequestBody CategoriesDTO categoriesDTO,
            BindingResult result
    ){
        if (result.hasErrors()){
            List<String> errorList = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(
                    CategoryResponse.builder()
                            .message(localizationUtils
                                    .getLocalizedMessage(MessageKeys.CHECK_CATEGORY_FAILED,errorList))
                            .build()
            );
        }
        Categories categories = categoriesService.createCategories(categoriesDTO);
        return ResponseEntity.ok(
                CategoryResponse.builder()
                        .categories(categories)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoriesDTO categoriesDTO){
        categoriesService.updateCategories(id,categoriesDTO);
        return ResponseEntity.ok(
                CategoryResponse.builder()
                .message(localizationUtils
                        .getLocalizedMessage(MessageKeys.UPDATE_CATEGORY_SUCCESSFULLY,id))
                .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory (@PathVariable Long id){
        categoriesService.deleteCategories(id);
        return ResponseEntity.ok(
                CategoryResponse.builder()
                        .message(localizationUtils
                                .getLocalizedMessage(MessageKeys.DELETE_CATEGORY_SUCCESSFULLY,id))
                        .build()
        );
    }

}
