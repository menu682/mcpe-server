package ua.pp.mcpe.server.service;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.JDBCException;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.exception.spi.SQLExceptionConverter;
import org.springframework.stereotype.Service;
import ua.pp.mcpe.server.config.security.UserDetailsImpl;
import ua.pp.mcpe.server.dto.EResponseMessage;
import ua.pp.mcpe.server.dto.MessageResponseDto;
import ua.pp.mcpe.server.dto.category.CategoryRequestDto;
import ua.pp.mcpe.server.dto.category.CategoryResponseDto;
import ua.pp.mcpe.server.exeptions.ConflictException;
import ua.pp.mcpe.server.exeptions.DataNotFoundException;
import ua.pp.mcpe.server.exeptions.EExceptionMessage;
import ua.pp.mcpe.server.persistance.converter.CategoryDtoConverter;
import ua.pp.mcpe.server.persistance.entity.CategoryEntity;
import ua.pp.mcpe.server.persistance.entity.ModEntity;
import ua.pp.mcpe.server.persistance.entity.PhotoEntity;
import ua.pp.mcpe.server.persistance.repository.CategoryRepository;
import ua.pp.mcpe.server.persistance.repository.ModRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@Slf4j
public class CategoryService {

    CategoryRepository categoryRepository;
    ModRepository modRepository;
    PhotoService photoService;
    CategoryDtoConverter converter;

    public CategoryService(CategoryRepository categoryRepository,
                           ModRepository modRepository,
                           PhotoService photoService,
                           CategoryDtoConverter converter) {
        this.categoryRepository = categoryRepository;
        this.modRepository = modRepository;
        this.photoService = photoService;
        this.converter = converter;
    }

    public Set<CategoryResponseDto> getAllCategory() {

        Set<CategoryResponseDto> categoryResponseDtoSet = new HashSet<>();

        List<CategoryEntity> categoryEntityList = categoryRepository.findAll();

        categoryEntityList.forEach(categoryEntity -> {
            categoryResponseDtoSet.add(converter.entityToResponse(categoryEntity));
        });

        return categoryResponseDtoSet;

    }

    public CategoryResponseDto getCategoryById(Long id){

        CategoryEntity categoryEntity = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    throw new DataNotFoundException(EExceptionMessage.CATEGORY_NO_SUCH.getMessage());
                });

        return converter.entityToResponse(categoryEntity);

    }

    public CategoryResponseDto addCategory(CategoryRequestDto categoryRequestDto,
                                           UserDetailsImpl user){

        Optional<CategoryEntity> categoryEntity =
                categoryRepository.getCategoryEntityByName(categoryRequestDto.getName());

        if(categoryEntity.isPresent()){
            throw new ConflictException(EExceptionMessage.CATEGORY_ALREADY_EXISTS.getMessage());
        }

        CategoryEntity category = converter.requestToEntity(categoryRequestDto);
        category.setPhotos(new HashSet<>());

        log.info("Add category: " + category.getName() + "; user: " + user.getUsername());

        CategoryEntity savedCategory = categoryRepository.save(category);
        return converter.entityToResponse(savedCategory);

    }

    public CategoryResponseDto patchCategory(CategoryRequestDto categoryRequestDto,
                                             UserDetailsImpl user){

        CategoryEntity category = categoryRepository.findById(categoryRequestDto.getId())
                .orElseThrow(() -> {
                    throw new DataNotFoundException(EExceptionMessage.CATEGORY_NO_SUCH.getMessage());
                });

        category.setName(categoryRequestDto.getName());
        category.setParent(categoryRequestDto.getParent());

        categoryRepository.save(category);
        log.info("Patch category: " + category.getName() + "; user: " + user.getUsername());
        return converter.entityToResponse(category);

    }

    public MessageResponseDto deleteCategory(CategoryRequestDto categoryRequestDto,
                                             UserDetailsImpl user){

        CategoryEntity category = categoryRepository.findById(categoryRequestDto.getId())
                .orElseThrow(() -> {
                    throw new DataNotFoundException(EExceptionMessage.CATEGORY_NO_SUCH.getMessage());
                });

        List<CategoryEntity> parentCategories = categoryRepository.getAllByParent(category.getId());

        if (!parentCategories.isEmpty()){
            throw new ConflictException(EExceptionMessage.CATEGORY_IS_PARENT.getMessage());
        }

        Set<ModEntity> modEntityList = modRepository.findModEntitiesByCategory(category);

        if(!modEntityList.isEmpty()){
            throw new ConflictException(EExceptionMessage.CATEGORY_IS_NOT_EMPTY.getMessage());
        }

        category.getPhotos().forEach(photoEntity ->
            photoService.removeCategoryPhoto(category.getId(), photoEntity.getName(), user)
        );

        categoryRepository.delete(category);

        log.info("Delete category: " + category.getName() + "; user: " + user.getUsername());

        return new MessageResponseDto(EResponseMessage.CATEGORY_DELETED.getMessage());

    }
}
