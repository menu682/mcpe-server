package ua.pp.mcpe.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ua.pp.mcpe.server.config.security.UserDetailsImpl;
import ua.pp.mcpe.server.dto.EResponseMessage;
import ua.pp.mcpe.server.dto.MessageResponseDto;
import ua.pp.mcpe.server.dto.mod.ModPaginationDto;
import ua.pp.mcpe.server.dto.mod.ModRequestDto;
import ua.pp.mcpe.server.dto.mod.ModResponseDto;
import ua.pp.mcpe.server.exeptions.ConflictException;
import ua.pp.mcpe.server.exeptions.DataNotFoundException;
import ua.pp.mcpe.server.exeptions.EExceptionMessage;
import ua.pp.mcpe.server.persistance.converter.ModDtoConverter;
import ua.pp.mcpe.server.persistance.entity.CategoryEntity;
import ua.pp.mcpe.server.persistance.entity.ModEntity;
import ua.pp.mcpe.server.persistance.repository.CategoryRepository;
import ua.pp.mcpe.server.persistance.repository.ModRepository;

import javax.transaction.Transactional;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class ModService {

    ModRepository modRepository;
    CategoryRepository categoryRepository;
    PhotoService photoService;
    ModDtoConverter converter;

    public ModService(ModRepository modRepository,
                      CategoryRepository categoryRepository,
                      PhotoService photoService,
                      ModDtoConverter converter) {
        this.modRepository = modRepository;
        this.categoryRepository = categoryRepository;
        this.photoService = photoService;
        this.converter = converter;
    }

    public Set<ModResponseDto> getModByCategory(Long categoryId){

        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    throw new DataNotFoundException(EExceptionMessage.CATEGORY_NO_SUCH.getMessage());
                });

        Set<ModEntity> modEntitySet = modRepository.findModEntitiesByCategory(category);
        Set<ModResponseDto> modResponseDtoSet = new HashSet<>();
        Set<ModResponseDto> finalModResponseDtoSet = modResponseDtoSet;
        modEntitySet.forEach(modEntity ->
            finalModResponseDtoSet.add(converter.entityToResponse(modEntity))
        );

        modResponseDtoSet = modResponseDtoSet.stream().sorted((o1, o2) ->
                o1.getId() > o2.getId() ? -1 : 0).collect(Collectors.toCollection(LinkedHashSet::new));

        return modResponseDtoSet;
    }

    public ModResponseDto getModById(Long id){

        ModEntity modEntity = modRepository.findById(id)
                .orElseThrow(() -> {
                    throw new DataNotFoundException(EExceptionMessage.MOD_NOT_FOUND.getMessage());
                });

        modEntity.setViews(modEntity.getViews() == null ? 1 : modEntity.getViews() + 1);
        modRepository.save(modEntity);

        return converter.entityToResponse(modEntity);
    }

    public List<ModResponseDto> getModByName(String name){

        List<ModEntity> modEntityList =
                modRepository.findAllByNameContainsIgnoreCase(name);

        if(modEntityList.isEmpty()){
            throw new ConflictException(EExceptionMessage.MOD_NOT_FOUND.getMessage());
        }

        List<ModResponseDto> modResponseDtoList = new ArrayList<>();

        modEntityList.forEach(modEntity -> {
            modResponseDtoList.add(converter.entityToResponse(modEntity));
        });

        return modResponseDtoList;
    }

    public ModPaginationDto getModsFromCategoryPagination(Long categoryId,
                                                          Integer pageNumber,
                                                          Integer size){

        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    throw new DataNotFoundException(EExceptionMessage.CATEGORY_NO_SUCH.getMessage());
                });

        Pageable paging = PageRequest.of(pageNumber - 1, size, Sort.by("id").descending());

        Page<ModEntity> page = modRepository.findModEntitiesByCategory(category, paging);

        if (page.isEmpty()){
            throw new DataNotFoundException(EExceptionMessage.MOD_NOT_FOUND.getMessage());
        }

        ModPaginationDto modPaginationDto = new ModPaginationDto();
        modPaginationDto.setTotalPages(page.getTotalPages());
        modPaginationDto.setCurrentPage(page.getNumber());
        modPaginationDto.setTotalElements(page.getTotalElements());
        modPaginationDto.setModResponseDtoSet(new ArrayList<>());

        page.getContent().forEach(modEntity ->
            modPaginationDto.getModResponseDtoSet().add(converter.entityToResponse(modEntity))
        );

        return modPaginationDto;
    }

    public ModResponseDto addMod(ModRequestDto modRequestDto, UserDetailsImpl user){

        Optional<ModEntity> modEntity =
                modRepository.getModEntityByName(modRequestDto.getName());

        if(modEntity.isPresent()){
            throw new ConflictException(EExceptionMessage.MOD_ALREADY_EXISTS.getMessage());
        }

        ModEntity newModEntity = converter.requestToEntity(modRequestDto);
        ModEntity saveModEntity = modRepository.save(newModEntity);
        log.info("Add mod: " + newModEntity.getName() + "; user: " + user.getUsername());

        return converter.entityToResponse(saveModEntity);
    }

    public ModResponseDto patchMod(ModRequestDto modRequestDto,
                                             UserDetailsImpl user){

        ModEntity mod = modRepository.findById(modRequestDto.getId())
                .orElseThrow(() -> {
                    throw new DataNotFoundException(EExceptionMessage.MOD_NOT_FOUND.getMessage());
                });

        CategoryEntity category = categoryRepository.findById(modRequestDto.getCategory())
                        .orElseThrow(() -> {
                            throw new DataNotFoundException(EExceptionMessage.CATEGORY_NO_SUCH.getMessage());
                        });

        mod.setName(modRequestDto.getName());
        mod.setCategory(category);
        mod.setDescription(modRequestDto.getDescription());

        modRepository.save(mod);
        log.info("Patch mod: " + mod.getName() + "; user: " + user.getUsername());
        return converter.entityToResponse(mod);

    }

    public MessageResponseDto deleteMod(ModRequestDto modRequestDto,
                                             UserDetailsImpl user){

        ModEntity mod = modRepository.findById(modRequestDto.getId())
                .orElseThrow(() -> {
                    throw new DataNotFoundException(EExceptionMessage.MOD_NOT_FOUND.getMessage());
                });

        mod.getPhotos().forEach(photoEntity ->
                photoService.removeModPhoto(mod.getId(), photoEntity.getName(), user)
        );

        modRepository.delete(mod);
        log.info("Delete mod: " + mod.getName() + "; user: " + user.getUsername());

        return new MessageResponseDto(EResponseMessage.MOD_DELETED.getMessage());

    }

}
