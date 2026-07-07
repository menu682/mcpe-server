package ua.pp.mcpe.server.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.pp.mcpe.server.persistance.entity.ModEntity;
import ua.pp.mcpe.server.persistance.entity.VersionEntity;

import java.util.Optional;
import java.util.Set;

@Repository
public interface VersionRepository extends JpaRepository<VersionEntity, Long> {

    Optional<VersionEntity> findVersionEntityByName(String name);

    @Query(nativeQuery = true, value = "select m.id from mod as m full join mod_files as mf on m.id = mf.mod_entity_id  full join file as f on mf.files_id = f.id where f.version_id = ?1")
    Optional<Set<Long>> findModEntitiesByFileVersionId(Long requestId);


}
