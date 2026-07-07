package ua.pp.mcpe.server.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.pp.mcpe.server.config.security.UserDetailsImpl;
import ua.pp.mcpe.server.dto.MessageResponseDto;
import ua.pp.mcpe.server.dto.category.CategoryRequestDto;
import ua.pp.mcpe.server.dto.category.CategoryResponseDto;
import ua.pp.mcpe.server.exeptions.EExceptionMessage;
import ua.pp.mcpe.server.exeptions.ForbiddenException;
import ua.pp.mcpe.server.service.CategoryService;

import java.util.Set;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api_v1/category")
public class CategoryController {

    private final String USER_TOKEN;
    private final CategoryService categoryService;

    public CategoryController(@Value("${app.userAccessToken}") String USER_TOKEN,
                              CategoryService categoryService) {
        this.USER_TOKEN = USER_TOKEN;
        this.categoryService = categoryService;
    }

    private void checkUserAccessToken(String token){
        if(token.isBlank() || !token.equals(USER_TOKEN)){
            throw new ForbiddenException(EExceptionMessage.UNAUTHORIZED.getMessage());
        }
    }

    @GetMapping()
    public Set<CategoryResponseDto> getAllCategory(@RequestParam String token){

        checkUserAccessToken(token);

        return categoryService.getAllCategory();
    }

    @GetMapping("/{id}")
    public CategoryResponseDto getCategoryById(@PathVariable Long id,
                                               @RequestParam String token){
        checkUserAccessToken(token);

        return categoryService.getCategoryById(id);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public CategoryResponseDto addCategory(@RequestBody CategoryRequestDto categoryRequestDto,
                                           @AuthenticationPrincipal UserDetailsImpl user){

        return categoryService.addCategory(categoryRequestDto, user);

    }

    @Secured("ROLE_ADMIN")
    @PatchMapping
    public CategoryResponseDto patchCategory(@RequestBody CategoryRequestDto categoryRequestDto,
                                             @AuthenticationPrincipal UserDetailsImpl user){

        return categoryService.patchCategory(categoryRequestDto, user);

    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping
    public MessageResponseDto deleteCategory(@RequestBody CategoryRequestDto categoryRequestDto,
                                             @AuthenticationPrincipal UserDetailsImpl user){

        return categoryService.deleteCategory(categoryRequestDto, user);

    }

}
