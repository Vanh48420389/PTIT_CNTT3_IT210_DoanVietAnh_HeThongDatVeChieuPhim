package va.edu.rikkei.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import va.edu.rikkei.model.entity.Showtime;
import java.time.LocalDateTime;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    // Tìm tất cả suất chiếu của một phòng trong khoảng thời gian 24h quanh giờ bắt đầu (Dùng cho Xếp lịch)
    @Query("SELECT s FROM Showtime s WHERE s.room.id = :roomId " +
            "AND s.startTime >= :startOfDay AND s.startTime <= :endOfDay")
    List<Showtime> findShowtimesByRoomAndDate(
            @Param("roomId") Long roomId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);

    // Tìm tất cả suất chiếu của một phim
    List<Showtime> findByMovieId(Long movieId);

    // THÊM DÒNG NÀY VÀO LÀ HẾT LỖI ĐỎ Ở CONTROLLER NÈ BRO:
    // Lọc suất chiếu của phim X, thời gian > thời gian hiện tại, sắp xếp giờ tăng dần
    List<Showtime> findByMovieIdAndStartTimeAfterOrderByStartTimeAsc(Long movieId, LocalDateTime currentTime);
}