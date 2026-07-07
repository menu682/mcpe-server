package ua.pp.mcpe.server.persistance.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ua.pp.mcpe.server.dto.photo.PhotoRequestDto;
import ua.pp.mcpe.server.dto.photo.PhotoResponseDto;
import ua.pp.mcpe.server.persistance.entity.PhotoEntity;

@Component
public class PhotoDtoConverter {

    public PhotoEntity requestToEntity(PhotoRequestDto request){

        return PhotoEntity.builder()
                .link(request.getLink())
                .build();
    }

    public PhotoResponseDto entityToResponse(PhotoEntity entity){

        return PhotoResponseDto.builder()
                .id(entity.getId())
                .created(entity.getCreated())
                .updated(entity.getUpdated())
                .name(entity.getName())
                .link(entity.getLink())
                .build();
    }

}
