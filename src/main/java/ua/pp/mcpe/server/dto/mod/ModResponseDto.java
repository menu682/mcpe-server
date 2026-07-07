package ua.pp.mcpe.server.dto.mod;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ua.pp.mcpe.server.dto.category.CategoryResponseDto;
import ua.pp.mcpe.server.dto.file.FileResponseDto;
import ua.pp.mcpe.server.dto.photo.PhotoResponseDto;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModResponseDto {

    @JsonProperty("mod_id")
    Long id;
    LocalDateTime created;
    LocalDateTime updated;

    @JsonProperty("mod_name")
    String name;

    @JsonProperty("mod_description")
    String description;

    @JsonProperty("mod_category")
    CategoryResponseDto category;

    @JsonProperty("mod_views")
    Integer views;

    @JsonProperty("mod_downloads")
    Integer downloads;

    @JsonProperty("mod_photos")
    List<PhotoResponseDto> photos;

    @JsonProperty("mod_files")
    List<FileResponseDto> files;

}
