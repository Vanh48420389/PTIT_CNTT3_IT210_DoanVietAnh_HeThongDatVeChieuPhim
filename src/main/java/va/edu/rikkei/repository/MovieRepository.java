package va.edu.rikkei.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import va.edu.rikkei.model.entity.Movie;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    // JpaRepository đã cung cấp sẵn các hàm như findAll(), findById(), save(), deleteById()...
    // Bạn có thể định nghĩa thêm các hàm tìm kiếm tùy chỉnh ở đây nếu cần.
    // Ví dụ: Tìm phim theo trạng thái
     List<Movie> findByStatus(Integer status);
}