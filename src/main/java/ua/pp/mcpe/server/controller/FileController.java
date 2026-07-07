package ua.pp.mcpe.server.controller;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ua.pp.mcpe.server.config.security.UserDetailsImpl;
import ua.pp.mcpe.server.dto.file.FileRequestDto;
import ua.pp.mcpe.server.dto.mod.ModRequestDto;
import ua.pp.mcpe.server.dto.mod.ModResponseDto;
import ua.pp.mcpe.server.dto.version.VersionRequestDto;
import ua.pp.mcpe.server.exeptions.EExceptionMessage;
import ua.pp.mcpe.server.exeptions.ForbiddenException;
import ua.pp.mcpe.server.service.FileService;

import javax.servlet.ServletContext;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api_v1/file")
public class FileController {


    private final String USER_TOKEN;
    private final FileService fileService;

    private ServletContext servletContext;

    public FileController(@Value("${app.userAccessToken}") String USER_TOKEN,
                          FileService fileService,
                          ServletContext servletContext) {
        this.USER_TOKEN = USER_TOKEN;
        this.fileService = fileService;
        this.servletContext = servletContext;
    }

    private void checkUserAccessToken(String token){
        if(token.isBlank() || !token.equals(USER_TOKEN)){
            throw new ForbiddenException(EExceptionMessage.UNAUTHORIZED.getMessage());
        }
    }

    @Secured({"ROLE_ADMIN"})
    @PostMapping(value = "/upload",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ModResponseDto uploadModFile(@RequestPart("mod") @Valid @NotNull @NotBlank ModRequestDto mod,
                                              @RequestPart("version") @Valid @NotNull @NotBlank VersionRequestDto version,
                                              @RequestPart("file")  @Valid @NotNull @NotBlank MultipartFile file,
                                              @AuthenticationPrincipal UserDetailsImpl user){

        return fileService.uploadModFile(mod, version, file,  user);

    }

    private MediaType getMediaType(String fileName){

        String mineType = servletContext.getMimeType(fileName);

        try {
            MediaType mediaType = MediaType.parseMediaType(mineType);
            return mediaType;
        } catch (Exception e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    @GetMapping("/download/{modId}/{version}/{fileName}")
    public ResponseEntity getModFile(@RequestParam String token,
                                      @PathVariable String fileName,
                                      @PathVariable String version,
                                      @PathVariable Long modId){

        checkUserAccessToken(token);

        ByteArrayResource resource = fileService.downloadFile(modId, version, fileName);

        MediaType mediaType = getMediaType(fileName);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.ACCEPT_CHARSET, "utf-8")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(mediaType)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @Secured({"ROLE_ADMIN"})
    @DeleteMapping("/remove/{modId}")
    public ModResponseDto removeModFile(@PathVariable Long modId,
                                        @RequestBody FileRequestDto fileRequestDto,
                                        @AuthenticationPrincipal UserDetailsImpl user){

        return fileService.removeModFile(modId, fileRequestDto, user);

    }
}
