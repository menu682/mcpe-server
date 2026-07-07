package ua.pp.mcpe.server.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.pp.mcpe.server.persistance.entity.CategoryEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {


        Optional<CategoryEntity> getCategoryEntityByName(String name);

        List<CategoryEntity> getAllByParent(Long parent);

        CategoryEntity getCategoryById(Long id);

}
