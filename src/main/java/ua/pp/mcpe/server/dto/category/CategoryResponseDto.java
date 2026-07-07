package ua.pp.mcpe.server.dto.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ua.pp.mcpe.server.dto.photo.PhotoResponseDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponseDto {

    @JsonProperty("category_id")
    Long id;

    LocalDateTime created;

    LocalDateTime updated;

    @JsonProperty("category_name")
    String name;

    Long parent;

    @NotNull
    @JsonProperty("category_photos")
    List<PhotoResponseDto> photos;

}
