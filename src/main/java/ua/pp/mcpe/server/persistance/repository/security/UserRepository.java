package ua.pp.mcpe.server.persistance.repository.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.pp.mcpe.server.persistance.entity.security.UserEntity;


import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByName(String name);

    Boolean existsByName(String name);


}
