package ua.pp.mcpe.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.pp.mcpe.server.config.security.UserDetailsImpl;
import ua.pp.mcpe.server.dto.category.CategoryRequestDto;
import ua.pp.mcpe.server.dto.category.CategoryResponseDto;
import ua.pp.mcpe.server.dto.mod.ModRequestDto;
import ua.pp.mcpe.server.dto.mod.ModResponseDto;
import ua.pp.mcpe.server.exeptions.BadDataRequestException;
import ua.pp.mcpe.server.exeptions.DataNotFoundException;
import ua.pp.mcpe.server.exeptions.EExceptionMessage;
import ua.pp.mcpe.server.persistance.converter.CategoryDtoConverter;
import ua.pp.mcpe.server.persistance.converter.ModDtoConverter;
import ua.pp.mcpe.server.persistance.entity.CategoryEntity;
import ua.pp.mcpe.server.persistance.entity.ModEntity;
import ua.pp.mcpe.server.persistance.entity.PhotoEntity;
import ua.pp.mcpe.server.persistance.repository.CategoryRepository;
import ua.pp.mcpe.server.persistance.repository.ModRepository;
import ua.pp.mcpe.server.persistance.repository.PhotoRepository;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@Transactional
@Slf4j
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final CategoryRepository categoryRepository;
    private final ModRepository modRepository;

    private final CategoryDtoConverter categoryConverter;
    private final ModDtoConverter modConverter;
    private final String uploadPhotoPath;


    public PhotoService(PhotoRepository photoRepository,
                        CategoryRepository categoryRepository,
                        ModRepository modRepository,
                        CategoryDtoConverter categoryConverter,
                        ModDtoConverter modConverter,
                        @Value("${app.uploadPhotoPath}") String uploadPhotoPath) {
        this.photoRepository = photoRepository;
        this.categoryRepository = categoryRepository;
        this.modRepository = modRepository;
        this.categoryConverter = categoryConverter;
        this.modConverter = modConverter;
        this.uploadPhotoPath = uploadPhotoPath;
    }


    public CategoryResponseDto uploadCategoryPhoto(CategoryRequestDto categoryRequestDto,
                                                   MultipartFile file,
                                                   UserDetailsImpl user){

        CategoryEntity category = categoryRepository.findById(categoryRequestDto.getId())
                .orElseThrow(() -> {
                    throw new DataNotFoundException(EExceptionMessage.CATEGORY_NO_SUCH.getMessage());
                });

        String fileLink = createUploadFileLink(file, category.getId());
        String fileDirectory = "category/" + category.getId() + "/";

        PhotoEntity uploadedPhoto = photoRepository.save(createPhotoEntity(fileLink));

        category.getPhotos().add(uploadedPhoto);
        CategoryEntity updatedCategory = categoryRepository.save(category);

        saveFile(fileLink, fileDirectory, file);
        log.info("Upload category photo: " + uploadedPhoto.getName() + " user: " + user.getUsername());
        return categoryConverter.entityToResponse(updatedCategory);
    }

    public BufferedImage downloadCategoryPhoto(Long categoryId, String fileName) {

        Path path = Path.of( uploadPhotoPath, "category", categoryId.toString(), fileName);

        File file = new File(path.toString());

        try (FileInputStream fis = new FileInputStream(file)) {
            BufferedImage bufferedImage = ImageIO.read(fis);
            return bufferedImage;
        } catch (FileNotFoundException e) {
            throw new DataNotFoundException(EExceptionMessage.FILE_NOT_FOUND.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public CategoryResponseDto removeCategoryPhoto(Long categoryId,
                                                   String fileName,
                                                   UserDetailsImpl user) {

        CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    throw new DataNotFoundException(EExceptionMessage.CATEGORY_NO_SUCH.getMessage());
                });


        PhotoEntity photoEntity = photoRepository.findByName(fileName)
                        .orElseThrow(() -> {
                            throw new DataNotFoundException(EExceptionMessage.PHOTO_NOT_FOUND.getMessage());
                        });

        photoRepository.delete(photoEntity);

        categoryEntity.getPhotos().remove(photoEntity);
        categoryRepository.save(categoryEntity);

        Path path = Path.of( uploadPhotoPath, "category", categoryId.toString(), fileName);
        removePhoto(path.toString());

        Path dirPath = Path.of(uploadPhotoPath, "category", categoryId.toString());
        File dir = new File(dirPath.toString());
        if(dir.isDirectory() && dir.listFiles().length == 0 ){
            dir.delete();
        }

        log.info("Deleted category photo: " + photoEntity.getName() + " user: " + user.getUsername());
        return categoryConverter.entityToResponse(categoryEntity);
    }

    public ModResponseDto uploadModPhoto(ModRequestDto modRequestDto,
                                         MultipartFile file,
                                         UserDetailsImpl user){

        ModEntity mod = modRepository.findById(modRequestDto.getId())
                .orElseThrow(() -> {
                    throw new DataNotFoundException(EExceptionMessage.MOD_NOT_FOUND.getMessage());
                });

        String fileLink = createUploadFileLink(file, mod.getId());
        String fileDirectory = "mod/" + mod.getId() + "/";

        PhotoEntity uploadedPhoto = photoRepository.save(createPhotoEntity(fileLink));

        mod.getPhotos().add(uploadedPhoto);
        ModEntity updatedMod = modRepository.save(mod);

        saveFile(fileLink, fileDirectory, file);
        log.info("Upload mod photo: " + uploadedPhoto.getName()
                + " for mod: " + updatedMod.getName()
                + " user: " + user.getUsername());
        return modConverter.entityToResponse(updatedMod);
    }

    public BufferedImage downloadModPhoto(Long modId, String fileName) {

        Path path = Path.of( uploadPhotoPath, "mod", modId.toString(), fileName);

        File file = new File(path.toString());

        try (FileInputStream fis = new FileInputStream(file)) {
            BufferedImage bufferedImage = ImageIO.read(fis);
            return bufferedImage;
        } catch (FileNotFoundException e) {
            throw new DataNotFoundException(EExceptionMessage.FILE_NOT_FOUND.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public ModResponseDto removeModPhoto(Long modId,
                                                   String fileName,
                                                   UserDetailsImpl user) {

        ModEntity modEntity = modRepository.findById(modId)
                .orElseThrow(() -> {
                    throw new DataNotFoundException(EExceptionMessage.MOD_NOT_FOUND.getMessage());
                });


        PhotoEntity photoEntity = photoRepository.findByName(fileName)
                .orElseThrow(() -> {
                    throw new DataNotFoundException(EExceptionMessage.PHOTO_NOT_FOUND.getMessage());
                });

        photoRepository.delete(photoEntity);

        modEntity.getPhotos().remove(photoEntity);
        modRepository.save(modEntity);

        Path path = Path.of( uploadPhotoPath, "mod", modId.toString(), fileName);
        removePhoto(path.toString());

        Path dirPath = Path.of(uploadPhotoPath, "mod", modId.toString());
        File dir = new File(dirPath.toString());
        if(dir.isDirectory() && dir.listFiles().length == 0 ){
            dir.delete();
        }

        log.info("Deleted mod photo: " + photoEntity.getName() + " user: " + user.getUsername());
        return modConverter.entityToResponse(modEntity);
    }

    private String createUploadFileLink(MultipartFile multipartFile,
                                        Long id) {

        String uploadFileName = multipartFile.getOriginalFilename();

        assert uploadFileName != null : EExceptionMessage.INVALID_FILE_NAME.getMessage();
        String fileSufix = uploadFileName.substring(uploadFileName.lastIndexOf(".") + 1);

        if (fileSufix.equals("jpg")) {
            fileSufix = "jpeg";
        } else if (!fileSufix.equals("png") && !fileSufix.equals("gif") && !fileSufix.equals("bmp")) {
            throw new BadDataRequestException(EExceptionMessage.UNSUPPORTED_FILE_FORMAT.getMessage());
        }

        StringBuilder fileLink = new StringBuilder();
        fileLink.append(id);
        fileLink.append("_");
        fileLink.append(uploadFileName, 0, uploadFileName.indexOf("."));
        fileLink.append("_");
        fileLink.append(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        fileLink.append(".");
        fileLink.append(fileSufix);

        return fileLink.toString();

    }

    private void saveFile(String fileLink,
                          String fileDirectory,
                          MultipartFile uploadFile) {

        File directory = new File(uploadPhotoPath + fileDirectory);

        if(!directory.exists()){
            try {
                Files.createDirectories(Paths.get(uploadPhotoPath + fileDirectory));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        File photo = new File(directory + "/" + fileLink);

        try (FileOutputStream fileOutputStream = new FileOutputStream(photo)) {
            fileOutputStream.write(uploadFile.getBytes());
        } catch (FileNotFoundException e) {
            throw new DataNotFoundException(EExceptionMessage.FILE_NOT_FOUND.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void removePhoto(String path) {

        File photo = new File(path);

        try {
            photo.delete();
        } catch (RuntimeException e) {
            new DataNotFoundException(EExceptionMessage.FILE_NOT_FOUND.getMessage());
        }
    }

    private PhotoEntity createPhotoEntity(String fileLink){
        PhotoEntity photoEntity = new PhotoEntity();
        photoEntity.setName(fileLink.substring(fileLink.lastIndexOf("/") + 1));
        photoEntity.setLink(fileLink);

        return photoEntity;
    }

}
