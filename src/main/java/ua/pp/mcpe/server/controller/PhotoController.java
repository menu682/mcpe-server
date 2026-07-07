package ua.pp.mcpe.server.controller;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ua.pp.mcpe.server.config.security.UserDetailsImpl;
import ua.pp.mcpe.server.dto.category.CategoryRequestDto;
import ua.pp.mcpe.server.dto.category.CategoryResponseDto;
import ua.pp.mcpe.server.dto.mod.ModRequestDto;
import ua.pp.mcpe.server.dto.mod.ModResponseDto;
import ua.pp.mcpe.server.exeptions.EExceptionMessage;
import ua.pp.mcpe.server.exeptions.ForbiddenException;
import ua.pp.mcpe.server.service.PhotoService;

import javax.imageio.ImageIO;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.io.IOException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api_v1/photo")
public class PhotoController {

    private final String USER_TOKEN;
    private final PhotoService photoService;

    public PhotoController(@Value("${app.userAccessToken}") String USER_TOKEN,
                            PhotoService photoService) {
        this.USER_TOKEN = USER_TOKEN;
        this.photoService = photoService;
    }


    private void checkUserAccessToken(String token){
        if(token.isBlank() || !token.equals(USER_TOKEN)){
            throw new ForbiddenException(EExceptionMessage.UNAUTHORIZED.getMessage());
        }
    }

    @GetMapping("/category/getphoto/{categoryId}/{fileName}")
    public ResponseEntity getCategoryPhoto(@RequestParam String token,
                                           @PathVariable String fileName,
                                           @PathVariable Long categoryId){

        checkUserAccessToken(token);

        BufferedImage bufferedImage = photoService.downloadCategoryPhoto(categoryId, fileName);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(baos.toByteArray());
    }

    @GetMapping("/mod/getphoto/{modId}/{fileName}")
    public ResponseEntity getModPhoto(@RequestParam String token,
                                           @PathVariable String fileName,
                                           @PathVariable Long modId){

        checkUserAccessToken(token);

        BufferedImage bufferedImage = photoService.downloadModPhoto(modId, fileName);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(baos.toByteArray());
    }

    @Secured({"ROLE_ADMIN"})
    @PostMapping(value = "/category/upload",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public CategoryResponseDto uploadCategoryPhoto(@RequestPart("category") @Valid @NotNull @NotBlank CategoryRequestDto category,
                                           @RequestPart("file")  @Valid @NotNull @NotBlank MultipartFile file,
                                           @AuthenticationPrincipal UserDetailsImpl user){

        return photoService.uploadCategoryPhoto(category, file, user);

    }

    @Secured({"ROLE_ADMIN"})
    @PostMapping(value = "/mod/upload",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ModResponseDto uploadCategoryPhoto(@RequestPart("mod") @Valid @NotNull @NotBlank ModRequestDto mod,
                                              @RequestPart("file")  @Valid @NotNull @NotBlank MultipartFile file,
                                              @AuthenticationPrincipal UserDetailsImpl user){

        return photoService.uploadModPhoto(mod, file, user);

    }

    @Secured({"ROLE_ADMIN"})
    @DeleteMapping("/category/removephoto/{categoryId}/{fileName}")
    public CategoryResponseDto removeCategoryPhoto(@PathVariable String fileName,
                                                   @PathVariable Long categoryId,
                                                   @AuthenticationPrincipal UserDetailsImpl user){

        return photoService.removeCategoryPhoto(categoryId, fileName, user);

    }

    @Secured({"ROLE_ADMIN"})
    @DeleteMapping("/mod/removephoto/{modId}/{fileName}")
    public ModResponseDto removeModPhoto(@PathVariable String fileName,
                                         @PathVariable Long modId,
                                         @AuthenticationPrincipal UserDetailsImpl user){

        return photoService.removeModPhoto(modId, fileName, user);

    }

}
