package va.edu.rikkei.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import va.edu.rikkei.model.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}