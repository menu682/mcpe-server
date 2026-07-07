package ua.pp.mcpe.server.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.pp.mcpe.server.persistance.entity.FileEntity;
import ua.pp.mcpe.server.persistance.entity.VersionEntity;

import java.util.Optional;
import java.util.Set;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    Optional<FileEntity> findByName(String name);

    Set<FileEntity> findAllByVersion(VersionEntity version);



}
