package ua.pp.mcpe.server.persistance.converter;

import org.springframework.stereotype.Component;
import ua.pp.mcpe.server.dto.file.FileRequestDto;
import ua.pp.mcpe.server.dto.file.FileResponseDto;
import ua.pp.mcpe.server.persistance.entity.FileEntity;

@Component
public class FileDtoConverter {

    private VersionDtoConverter versionDtoConverter;

    public FileDtoConverter(VersionDtoConverter versionDtoConverter) {
        this.versionDtoConverter = versionDtoConverter;
    }

    public FileResponseDto entityToResponse(FileEntity entity){

        return FileResponseDto.builder()
                .id(entity.getId())
                .created(entity.getCreated())
                .updated(entity.getUpdated())
                .name(entity.getName())
                .link(entity.getLink())
                .version(versionDtoConverter.entityToResponse(entity.getVersion()))
                .build();
    }
}
