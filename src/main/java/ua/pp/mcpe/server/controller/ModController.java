package ua.pp.mcpe.server.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.pp.mcpe.server.config.security.UserDetailsImpl;
import ua.pp.mcpe.server.dto.MessageResponseDto;
import ua.pp.mcpe.server.dto.mod.ModPaginationDto;
import ua.pp.mcpe.server.dto.mod.ModRequestDto;
import ua.pp.mcpe.server.dto.mod.ModResponseDto;
import ua.pp.mcpe.server.exeptions.EExceptionMessage;
import ua.pp.mcpe.server.exeptions.ForbiddenException;
import ua.pp.mcpe.server.service.ModService;

import java.util.List;
import java.util.Set;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api_v1/mod")
public class ModController {

    private final String USER_TOKEN;
    private final ModService modService;

    public ModController(@Value("${app.userAccessToken}") String USER_TOKEN,
                         ModService modService) {
        this.USER_TOKEN = USER_TOKEN;
        this.modService = modService;
    }

    private void checkUserAccessToken(String token){
        if(token.isBlank() || !token.equals(USER_TOKEN)){
            throw new ForbiddenException(EExceptionMessage.UNAUTHORIZED.getMessage());
        }
    }

    @GetMapping("/category/{category_id}")
    public Set<ModResponseDto> getModByCategory(@PathVariable Long category_id,
                                                @RequestParam String token){
        checkUserAccessToken(token);

        return modService.getModByCategory(category_id);
    }

    @GetMapping("/page/{category_id}")
    public ModPaginationDto getModFromCategoryPagination(@PathVariable Long category_id,
                                                         @RequestParam String token,
                                                         @RequestParam Integer page,
                                                         @RequestParam Integer size){

        checkUserAccessToken(token);

        return modService.getModsFromCategoryPagination(category_id, page, size);

    }

    @GetMapping("/{mod_id}")
    public ModResponseDto getModById(@PathVariable Long mod_id,
                                     @RequestParam String token){
        checkUserAccessToken(token);

        return modService.getModById(mod_id);
    }

    @GetMapping("/name")
    public List<ModResponseDto> getModByName(@RequestParam String mod,
                                             @RequestParam String token){
        checkUserAccessToken(token);

        return modService.getModByName(mod);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public ModResponseDto addMod(@RequestBody ModRequestDto modRequestDto,
                                 @AuthenticationPrincipal UserDetailsImpl user){

        return modService.addMod(modRequestDto, user);

    }

    @Secured("ROLE_ADMIN")
    @PatchMapping
    public ModResponseDto patchMod(@RequestBody ModRequestDto modRequestDto,
                                             @AuthenticationPrincipal UserDetailsImpl user){

        return modService.patchMod(modRequestDto, user);

    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping
    public MessageResponseDto deleteMod(@RequestBody ModRequestDto modRequestDto,
                                             @AuthenticationPrincipal UserDetailsImpl user){

        return modService.deleteMod(modRequestDto, user);

    }

}
