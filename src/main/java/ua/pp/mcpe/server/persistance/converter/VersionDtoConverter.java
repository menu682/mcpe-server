package ua.pp.mcpe.server.persistance.converter;

import org.springframework.stereotype.Component;
import ua.pp.mcpe.server.dto.version.VersionRequestDto;
import ua.pp.mcpe.server.dto.version.VersionResponseDto;
import ua.pp.mcpe.server.persistance.entity.VersionEntity;

@Component
public class VersionDtoConverter {

    public VersionEntity requestToEntity(VersionRequestDto request){

        return VersionEntity.builder()
                .name(request.getName())
                .build();
    }

    public VersionResponseDto entityToResponse(VersionEntity entity){

        return VersionResponseDto.builder()
                .id(entity.getId())
                .created(entity.getCreated())
                .updated(entity.getUpdated())
                .name(entity.getName())
                .build();
    }


}
