package va.edu.rikkei.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import va.edu.rikkei.model.entity.Ticket;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Lấy lịch sử đặt vé của một khách hàng
    List<Ticket> findByUserId(Long userId);

    // Tìm toàn bộ vé đã được đặt cho một lịch chiếu (dùng để khóa các ghế đã có người mua)
    List<Ticket> findByShowtimeId(Long showtimeId);
}