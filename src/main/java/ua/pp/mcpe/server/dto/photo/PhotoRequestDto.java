package ua.pp.mcpe.server.dto.photo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PhotoRequestDto {

    @NotNull
    @JsonProperty("photo_link")
    String link;

}
