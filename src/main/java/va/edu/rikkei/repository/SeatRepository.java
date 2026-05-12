package va.edu.rikkei.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import va.edu.rikkei.model.entity.Seat;
import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    // Tìm toàn bộ danh sách ghế thuộc về một phòng chiếu cụ thể (rất cần khi vẽ sơ đồ phòng chiếu)
    List<Seat> findByRoomId(Long roomId);
    long countByRoomId(Long roomId);
}