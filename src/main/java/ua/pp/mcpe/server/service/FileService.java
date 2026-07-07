package ua.pp.mcpe.server.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.pp.mcpe.server.config.security.UserDetailsImpl;
import ua.pp.mcpe.server.dto.file.FileRequestDto;
import ua.pp.mcpe.server.dto.mod.ModRequestDto;
import ua.pp.mcpe.server.dto.mod.ModResponseDto;
import ua.pp.mcpe.server.dto.version.VersionRequestDto;
import ua.pp.mcpe.server.exeptions.ConflictException;
import ua.pp.mcpe.server.exeptions.DataNotFoundException;
import ua.pp.mcpe.server.exeptions.EExceptionMessage;
import ua.pp.mcpe.server.persistance.converter.FileDtoConverter;
import ua.pp.mcpe.server.persistance.converter.ModDtoConverter;
import ua.pp.mcpe.server.persistance.converter.VersionDtoConverter;
import ua.pp.mcpe.server.persistance.entity.FileEntity;
import ua.pp.mcpe.server.persistance.entity.ModEntity;
import ua.pp.mcpe.server.persistance.entity.VersionEntity;
import ua.pp.mcpe.server.persistance.repository.FileRepository;
import ua.pp.mcpe.server.persistance.repository.ModRepository;
import ua.pp.mcpe.server.persistance.repository.VersionRepository;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Transactional
@Slf4j
public class FileService {

    private final FileRepository fileRepository;
    private final ModRepository modRepository;
    private final VersionRepository versionRepository;

    private final ModDtoConverter modConverter;


    private final String uploadFilePath;


    public FileService(FileRepository fileRepository,
                       ModRepository modRepository,
                       VersionRepository versionRepository,
                       ModDtoConverter modConverter,
                       @Value("${app.uploadFilePath}") String uploadFilePath) {
        this.fileRepository = fileRepository;
        this.modRepository = modRepository;
        this.versionRepository = versionRepository;
        this.modConverter = modConverter;
        this.uploadFilePath = uploadFilePath;
    }

    private String createUploadFileLink(MultipartFile multipartFile,
                                        Long modId,
                                        Long version) {

        String uploadFileName = multipartFile.getOriginalFilename();

        assert uploadFileName != null : EExceptionMessage.INVALID_FILE_NAME.getMessage();

        StringBuilder fileLink = new StringBuilder();
        fileLink.append(modId);
        fileLink.append("_");
        fileLink.append(version);
        fileLink.append("_");
        fileLink.append(uploadFileName);

        return fileLink.toString();

    }

    private void saveFile(String fileLink,
                          String fileDirectory,
                          MultipartFile uploadFile) {

        File directory = new File(uploadFilePath + fileDirectory);

        if(!directory.exists()){
            try {
                Files.createDirectories(Paths.get(uploadFilePath + fileDirectory));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Path path = Path.of(uploadFilePath, fileDirectory, fileLink);

        File file = new File(path.toString());

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(uploadFile.getBytes());
        } catch (FileNotFoundException e) {
            throw new DataNotFoundException(EExceptionMessage.FILE_NOT_FOUND.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeFile(String path) {

        File file = new File(path);

        try {
            file.delete();
        } catch (RuntimeException e) {
            new DataNotFoundException(EExceptionMessage.FILE_NOT_FOUND.getMessage());
        }

    }

    private void removeDirectory(String directory){

        File dir = new File(directory);

        if(dir.isDirectory() && dir.listFiles().length == 0 ){
            dir.delete();
        }
    }

    private FileEntity createFileEntity(String fileLink, VersionEntity version){
        FileEntity fileEntity = new FileEntity();
        fileEntity.setName(fileLink.substring(fileLink.lastIndexOf("/") + 1));
        fileEntity.setLink(fileLink);
        fileEntity.setVersion(version);

        return fileEntity;
    }

    public ModResponseDto uploadModFile(ModRequestDto modRequestDto,
                                        VersionRequestDto versionRequestDto,
                                        MultipartFile file,
                                        UserDetailsImpl user){

        ModEntity mod = modRepository.findById(modRequestDto.getId())
                .orElseThrow(() -> {
                    throw new DataNotFoundException(EExceptionMessage.MOD_NOT_FOUND.getMessage());
                });

        VersionEntity version = versionRepository.findById(versionRequestDto.getId())
                .orElseThrow(() -> {
                    throw new DataNotFoundException(EExceptionMessage.VERSION_NOT_FOUND.getMessage());
                });

        String fileLink = createUploadFileLink(file, mod.getId(), version.getId());
        Path dirPath = Path.of("mods", mod.getId().toString(), version.getName());
        String fileDirectory = dirPath.toString();

        FileEntity uploadedFile = fileRepository.save(createFileEntity(fileLink, version));

        mod.getFiles().add(uploadedFile);
        ModEntity updatedMod = modRepository.save(mod);

        saveFile(fileLink, fileDirectory, file);
        log.info("Upload mod file: " + uploadedFile.getName()
                + " version: " + version.getName()
                + " for mod: " + updatedMod.getName()
                + " user: " + user.getUsername());
        return modConverter.entityToResponse(updatedMod);
    }

    public ByteArrayResource downloadFile(Long modId,
                                          String version,
                                          String fileName){

        ModEntity modEntity = modRepository.findById(modId)
                .orElseThrow(() -> {
                            throw new DataNotFoundException(EExceptionMessage.MOD_NOT_FOUND.getMessage());
                });

        modEntity.setDownloads(modEntity.getDownloads() == null ? 1 : modEntity.getDownloads() + 1);
        modRepository.save(modEntity);

        Path path = Path.of(uploadFilePath,"mods", modId.toString(), version, fileName);

        try {
            return new ByteArrayResource(Files.readAllBytes(path));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public ModResponseDto removeModFile(Long modId,
                                         FileRequestDto fileRequestDto,
                                         UserDetailsImpl user) {

        ModEntity modEntity = modRepository.findById(modId)
                .orElseThrow(() -> {
                    throw new DataNotFoundException(EExceptionMessage.MOD_NOT_FOUND.getMessage());
                });

        VersionEntity versionEntity = versionRepository.findById(fileRequestDto.getVersion())
                .orElseThrow(() -> {
                    throw new DataNotFoundException(EExceptionMessage.VERSION_NOT_FOUND.getMessage());
                });

        FileEntity fileEntity = fileRepository.findByName(fileRequestDto.getName())
                .orElseThrow(() -> {
                    throw new DataNotFoundException(EExceptionMessage.FILE_NOT_FOUND.getMessage());
                });

        if(!fileEntity.getVersion().equals(versionEntity)){
            throw new ConflictException(EExceptionMessage.FILE_NOT_MACH_VERSION.getMessage());
        }

        fileRepository.delete(fileEntity);

        modEntity.getFiles().remove(fileEntity);
        modRepository.save(modEntity);

        Path path = Path.of(uploadFilePath,"mods",
                modId.toString(), versionEntity.getName(), fileRequestDto.getName());

        Path dir = Path.of(uploadFilePath,"mods",
                modId.toString(), versionEntity.getName());
        removeFile(path.toString());
        removeDirectory(dir.toString());

        log.info("Deleted mod file: "
                + fileEntity.getName()
                + " version: "
                + versionEntity.getName()
                + " user: " + user.getUsername());

        return modConverter.entityToResponse(modEntity);
    }

}
