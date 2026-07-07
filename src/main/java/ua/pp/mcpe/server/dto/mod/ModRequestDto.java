package ua.pp.mcpe.server.dto.mod;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ua.pp.mcpe.server.dto.file.FileRequestDto;
import ua.pp.mcpe.server.dto.photo.PhotoRequestDto;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModRequestDto {

    @JsonProperty("mod_id")
    Long id;

    @NotNull
    @JsonProperty("mod_name")
    String name;

    @NotNull
    @JsonProperty("mod_description")
    String description;

    @NotNull
    @JsonProperty("category_id")
    Long category;

    @JsonProperty("mod_photos")
    List<PhotoRequestDto> photos;

    @JsonProperty("mod_files")
    List<FileRequestDto> files;

}
