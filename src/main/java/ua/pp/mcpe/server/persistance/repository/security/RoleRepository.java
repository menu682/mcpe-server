package ua.pp.mcpe.server.persistance.repository.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.pp.mcpe.server.persistance.ERole;
import ua.pp.mcpe.server.persistance.entity.security.RoleEntity;


import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByName(ERole name);

}
