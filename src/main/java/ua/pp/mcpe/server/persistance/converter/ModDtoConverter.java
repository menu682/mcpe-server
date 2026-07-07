package ua.pp.mcpe.server.persistance.converter;

import org.springframework.stereotype.Component;
import ua.pp.mcpe.server.dto.file.FileResponseDto;
import ua.pp.mcpe.server.dto.mod.ModRequestDto;
import ua.pp.mcpe.server.dto.mod.ModResponseDto;
import ua.pp.mcpe.server.dto.photo.PhotoRequestDto;
import ua.pp.mcpe.server.dto.photo.PhotoResponseDto;
import ua.pp.mcpe.server.exeptions.DataNotFoundException;
import ua.pp.mcpe.server.exeptions.EExceptionMessage;
import ua.pp.mcpe.server.persistance.entity.CategoryEntity;
import ua.pp.mcpe.server.persistance.entity.FileEntity;
import ua.pp.mcpe.server.persistance.entity.ModEntity;
import ua.pp.mcpe.server.persistance.entity.PhotoEntity;
import ua.pp.mcpe.server.persistance.repository.CategoryRepository;
import ua.pp.mcpe.server.persistance.repository.FileRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ModDtoConverter {

    PhotoDtoConverter photoDtoConverter;
    CategoryDtoConverter categoryDtoConverter;
    FileDtoConverter fileDtoConverter;
    CategoryRepository categoryRepository;
    FileRepository fileRepository;

    public ModDtoConverter(PhotoDtoConverter photoDtoConverter,
                           CategoryDtoConverter categoryDtoConverter,
                           FileDtoConverter fileDtoConverter,
                           CategoryRepository categoryRepository,
                           FileRepository fileRepository) {
        this.photoDtoConverter = photoDtoConverter;
        this.categoryDtoConverter = categoryDtoConverter;
        this.fileDtoConverter = fileDtoConverter;
        this.categoryRepository = categoryRepository;
        this.fileRepository = fileRepository;
    }


    public ModEntity requestToEntity(ModRequestDto request) {

        return ModEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(categoryRepository.getCategoryById(request.getCategory()))
                .build();
    }


    public ModResponseDto entityToResponse(ModEntity entity) {

        return ModResponseDto.builder()
                .id(entity.getId())
                .created(entity.getCreated())
                .updated(entity.getUpdated())
                .name(entity.getName())
                .description(entity.getDescription())
                .category(categoryDtoConverter.entityToResponse(entity.getCategory()))
                .views(entity.getViews())
                .downloads(entity.getDownloads())
                .photos(entity.getPhotos() != null ? entity.getPhotos().stream().map(photoEntity -> {
                    PhotoResponseDto photoResponseDto = photoDtoConverter.entityToResponse(photoEntity);
                    return photoResponseDto;
                }).toList() : new ArrayList<>())
                .files(entity.getFiles() != null ?entity.getFiles().stream().map(fileEntity -> {
                    FileResponseDto fileResponseDto = fileDtoConverter.entityToResponse(fileEntity);
                    return fileResponseDto;
                }).toList() : new ArrayList<>())
                .build();
    }


}
