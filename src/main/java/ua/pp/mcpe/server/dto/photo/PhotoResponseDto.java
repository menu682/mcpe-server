package ua.pp.mcpe.server.dto.photo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PhotoResponseDto {

    Long id;
    LocalDateTime created;
    LocalDateTime updated;

    @JsonProperty("photo_name")
    private String name;

    @JsonProperty("photo_link")
    String link;

}
