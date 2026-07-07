package ua.pp.mcpe.server.dto.version;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VersionResponseDto {

    @JsonProperty("version_id")
    Long id;
    LocalDateTime created;
    LocalDateTime updated;

    @JsonProperty("version_name")
    String name;

}
