package ua.pp.mcpe.server.dto.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ua.pp.mcpe.server.persistance.entity.VersionEntity;

import javax.validation.constraints.NotNull;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileRequestDto {

    @NotNull
    @JsonProperty("file_name")
    String name;

    @NotNull
    @JsonProperty("file_link")
    String link;

    @NotNull
    @JsonProperty("version_id")
    Long version;

}
