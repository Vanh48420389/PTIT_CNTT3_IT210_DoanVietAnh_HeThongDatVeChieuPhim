package va.edu.rikkei.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import va.edu.rikkei.model.entity.User;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Hàm này cực kỳ quan trọng để kiểm tra đăng nhập hoặc kiểm tra trùng lặp email
    Optional<User> findByEmail(String email);
}