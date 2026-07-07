package ua.pp.mcpe.server.persistance.converter;

import org.springframework.stereotype.Component;
import ua.pp.mcpe.server.dto.category.CategoryRequestDto;
import ua.pp.mcpe.server.dto.category.CategoryResponseDto;
import ua.pp.mcpe.server.dto.photo.PhotoResponseDto;
import ua.pp.mcpe.server.persistance.entity.CategoryEntity;

import java.util.ArrayList;

@Component
public class CategoryDtoConverter {

    PhotoDtoConverter photoDtoConverter;

    public CategoryDtoConverter(PhotoDtoConverter photoDtoConverter) {
        this.photoDtoConverter = photoDtoConverter;
    }

    public CategoryEntity requestToEntity(CategoryRequestDto request){

        return CategoryEntity.builder()
                .name(request.getName())
                .parent(request.getParent())
                .build();
    }

    public CategoryResponseDto entityToResponse(CategoryEntity entity){

        return CategoryResponseDto.builder()
                .id(entity.getId())
                .created(entity.getCreated())
                .updated(entity.getUpdated())
                .name(entity.getName())
                .parent(entity.getParent())
                .photos(entity.getPhotos() != null ? entity.getPhotos().stream().map(photoEntity -> {
                    PhotoResponseDto photoResponseDto = photoDtoConverter.entityToResponse(photoEntity);
                    return photoResponseDto;
                }).toList() : new ArrayList<>())
                .build();

    }

}
