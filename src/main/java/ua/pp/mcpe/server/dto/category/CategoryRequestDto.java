package ua.pp.mcpe.server.dto.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryRequestDto {

    @JsonProperty("category_id")
    Long id;

    @JsonProperty("category_name")
    String name;

    Long parent;

}
