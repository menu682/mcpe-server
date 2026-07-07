package ua.pp.mcpe.server.dto.mod;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ModPaginationDto {

    @JsonProperty("total_elements")
    Long totalElements;

    @JsonProperty("total_pages")
    Integer totalPages;

    @JsonProperty("current_page")
    Integer currentPage;

    @JsonProperty("mods")
    List<ModResponseDto> modResponseDtoSet;

}
