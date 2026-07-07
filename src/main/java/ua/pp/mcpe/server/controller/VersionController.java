package ua.pp.mcpe.server.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
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
import ua.pp.mcpe.server.dto.MessageResponseDto;
import ua.pp.mcpe.server.dto.mod.ModResponseDto;
import ua.pp.mcpe.server.dto.version.VersionRequestDto;
import ua.pp.mcpe.server.dto.version.VersionResponseDto;
import ua.pp.mcpe.server.exeptions.EExceptionMessage;
import ua.pp.mcpe.server.exeptions.ForbiddenException;
import ua.pp.mcpe.server.service.VersionService;

import java.util.List;
import java.util.Set;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api_v1/version")
public class VersionController {

    private final String USER_TOKEN;
    private final VersionService versionService;

    public VersionController(@Value("${app.userAccessToken}") String USER_TOKEN,
                             VersionService versionService) {
        this.USER_TOKEN = USER_TOKEN;
        this.versionService = versionService;
    }

    private void checkUserAccessToken(String token){
        if(token.isBlank() || !token.equals(USER_TOKEN)){
            throw new ForbiddenException(EExceptionMessage.UNAUTHORIZED.getMessage());
        }
    }

    @GetMapping("/{versionId}")
    public VersionResponseDto getVersion(@RequestParam String token,
                                         @PathVariable Long versionId){
        checkUserAccessToken(token);
        return versionService.getVersion(versionId);
    }

    @GetMapping
    public List<VersionResponseDto> getAllVersion(@RequestParam String token){

        checkUserAccessToken(token);
        return versionService.getAllVersion();
    }

    @GetMapping("/mods/{versionId}")
    public Set<ModResponseDto> getModsByVersions(@RequestParam String token,
                                                 @PathVariable Long versionId){

        checkUserAccessToken(token);
        return versionService.getModsByVersions(versionId);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public VersionResponseDto addVersion(@RequestBody VersionRequestDto versionRequestDto){

        return versionService.addVersion(versionRequestDto);
    }

    @Secured("ROLE_ADMIN")
    @PatchMapping
    public VersionResponseDto patchVersion(@RequestBody VersionRequestDto versionRequestDto){

        return versionService.patchVersion(versionRequestDto);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping
    public MessageResponseDto deleteVersion(@RequestBody VersionRequestDto versionRequestDto){

        return versionService.deleteVersion(versionRequestDto);
    }

}
