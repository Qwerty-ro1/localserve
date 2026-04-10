package com.localserve.localserve.controller;

import com.localserve.localserve.dto.ApiResponse;
import com.localserve.localserve.entity.MasterServiceCategory;
import com.localserve.localserve.repository.MasterServiceCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final MasterServiceCategoryRepository masterServiceCategoryRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MasterServiceCategory>>> getAllCategories() {
        List<MasterServiceCategory> categories = masterServiceCategoryRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("Categories fetched", categories));
    }
}