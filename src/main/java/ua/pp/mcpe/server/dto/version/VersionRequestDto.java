package ua.pp.mcpe.server.dto.version;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VersionRequestDto {

    @JsonProperty("version_id")
    Long id;

    @JsonProperty("version_name")
    String name;

}
