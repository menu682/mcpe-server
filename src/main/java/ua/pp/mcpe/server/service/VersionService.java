package ua.pp.mcpe.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.pp.mcpe.server.dto.EResponseMessage;
import ua.pp.mcpe.server.dto.MessageResponseDto;
import ua.pp.mcpe.server.dto.mod.ModResponseDto;
import ua.pp.mcpe.server.dto.version.VersionRequestDto;
import ua.pp.mcpe.server.dto.version.VersionResponseDto;
import ua.pp.mcpe.server.exeptions.ConflictException;
import ua.pp.mcpe.server.exeptions.DataNotFoundException;
import ua.pp.mcpe.server.exeptions.EExceptionMessage;
import ua.pp.mcpe.server.persistance.converter.ModDtoConverter;
import ua.pp.mcpe.server.persistance.converter.VersionDtoConverter;
import ua.pp.mcpe.server.persistance.entity.FileEntity;
import ua.pp.mcpe.server.persistance.entity.ModEntity;
import ua.pp.mcpe.server.persistance.entity.VersionEntity;
import ua.pp.mcpe.server.persistance.repository.FileRepository;
import ua.pp.mcpe.server.persistance.repository.ModRepository;
import ua.pp.mcpe.server.persistance.repository.VersionRepository;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@Slf4j
public class VersionService {

    private final VersionRepository versionRepository;
    private final FileRepository fileRepository;
    private final ModRepository modRepository;
    private final VersionDtoConverter versionConverter;
    private final ModDtoConverter modConverter;

    public VersionService(VersionRepository versionRepository,
                          FileRepository fileRepository,
                          ModRepository modRepository,
                          VersionDtoConverter versionConverter,
                          ModDtoConverter modConverter) {
        this.versionRepository = versionRepository;
        this.fileRepository = fileRepository;
        this.modRepository = modRepository;
        this.versionConverter = versionConverter;
        this.modConverter = modConverter;
    }

    public VersionResponseDto getVersion(Long id) {

        VersionEntity versionEntity = versionRepository.findById(id)
                .orElseThrow(() -> {
                    throw new DataNotFoundException(EExceptionMessage.VERSION_NOT_FOUND.getMessage());
                });

        return versionConverter.entityToResponse(versionEntity);
    }

    public List<VersionResponseDto> getAllVersion(){

        List<VersionEntity> versionEntityList = versionRepository.findAll();

        return versionEntityList.stream().map(
                versionConverter::entityToResponse
        ).toList();

    }

    public Set<ModResponseDto> getModsByVersions(Long versionId) {

        Set<Long> modEntityIdSet = versionRepository.findModEntitiesByFileVersionId(versionId)
                .orElseThrow(() -> {
                    throw new DataNotFoundException(EExceptionMessage.MOD_NOT_FOUND.getMessage());
                });

        List<ModEntity> modEntitySet = modRepository.findAllById(modEntityIdSet);

        Set<ModResponseDto> modResponseDtoSet = new HashSet<>();
        modEntitySet.forEach(
                modEntity -> modResponseDtoSet.add(modConverter.entityToResponse(modEntity))
        );

        return modResponseDtoSet;

    }


    public VersionResponseDto addVersion(VersionRequestDto versionRequestDto) {

        Optional<VersionEntity> versionEntity = versionRepository.findVersionEntityByName(versionRequestDto.getName());

        if (versionEntity.isPresent()) {
            throw new ConflictException(EExceptionMessage.VERSION_ALREADY_EXISTS.getMessage());
        }

        VersionEntity newVersionEntity = new VersionEntity(versionRequestDto.getName());

        VersionEntity savedVersionEntity = versionRepository.save(newVersionEntity);

        return versionConverter.entityToResponse(savedVersionEntity);

    }

    public VersionResponseDto patchVersion(VersionRequestDto versionRequestDto) {

        VersionEntity versionEntity = versionRepository.findById(versionRequestDto.getId())
                .orElseThrow(() -> {
                    throw new DataNotFoundException(EExceptionMessage.VERSION_NOT_FOUND.getMessage());
                });

        Optional<VersionEntity> checkVersion = versionRepository.findVersionEntityByName(versionRequestDto.getName());

        if(checkVersion.isPresent()){
            throw new ConflictException(EExceptionMessage.VERSION_ALREADY_EXISTS.getMessage());
        }

        versionEntity.setName(versionRequestDto.getName());

        versionRepository.save(versionEntity);

        return versionConverter.entityToResponse(versionEntity);

    }

    public MessageResponseDto deleteVersion(VersionRequestDto versionRequestDto) {

        VersionEntity versionEntity = versionRepository.findById(versionRequestDto.getId())
                .orElseThrow(() -> {
                    throw new DataNotFoundException(EExceptionMessage.VERSION_NOT_FOUND.getMessage());
                });

        Set<FileEntity> fileEntitySet = fileRepository.findAllByVersion(versionEntity);

        if(!fileEntitySet.isEmpty()){
            throw new ConflictException(EExceptionMessage.VERSION_CONTAINS_FILES.getMessage());
        }

        versionRepository.delete(versionEntity);

        return new MessageResponseDto(EResponseMessage.VERSION_DELETED.getMessage());
    }

}
