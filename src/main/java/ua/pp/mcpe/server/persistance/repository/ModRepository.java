package ua.pp.mcpe.server.persistance.repository;

import org.hibernate.annotations.OrderBy;
import org.hibernate.criterion.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.pp.mcpe.server.persistance.entity.CategoryEntity;
import ua.pp.mcpe.server.persistance.entity.ModEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ModRepository extends JpaRepository<ModEntity, Long> {

    Optional<ModEntity> getModEntityByName(String name);


    List<ModEntity> findAllByNameContainsIgnoreCase(String name);

    Set<ModEntity> findModEntitiesByCategory(CategoryEntity category);
    Page<ModEntity> findModEntitiesByCategory(CategoryEntity category, Pageable pageable);
}
