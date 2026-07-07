package ua.pp.mcpe.server.dto.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ua.pp.mcpe.server.dto.version.VersionResponseDto;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileResponseDto {

    Long id;

    LocalDateTime created;

    LocalDateTime updated;

    @JsonProperty("file_name")
    String name;

    @JsonProperty("file_link")
    String link;

    @JsonProperty("file_version")
    VersionResponseDto version;

}
