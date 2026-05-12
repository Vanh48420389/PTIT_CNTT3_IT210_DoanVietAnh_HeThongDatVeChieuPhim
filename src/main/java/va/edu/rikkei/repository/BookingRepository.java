package va.edu.rikkei.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import va.edu.rikkei.model.entity.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // 1. CORE-09: Dùng cho hàm Đặt vé (Chống tranh ghế, bỏ qua ghế đã hủy)
    boolean existsByShowtimeIdAndSeatIdAndStatus(Long showtimeId, Long seatId, String status);

    // 2. CORE-09: Lấy danh sách các vé ĐANG HOẠT ĐỘNG (PAID) để bôi đỏ ghế trên sơ đồ
    List<Booking> findByShowtimeIdAndStatus(Long showtimeId, String status);

    // 3. CORE-09: Đếm số vé ĐANG HOẠT ĐỘNG để tính toán xem phòng đã Hết vé chưa
    long countByShowtimeIdAndStatus(Long showtimeId, String status);

    // 4. CORE-07: Lấy lịch sử vé của User, sắp xếp giảm dần
    List<Booking> findByUserIdOrderByBookingDateDesc(Long userId);

    // 5. HƯỚNG 4 (MỞ RỘNG): Thống kê doanh thu theo phim (Không dùng vòng lặp Java)
    @Query("SELECT m.title, SUM(b.price) " +
            "FROM Booking b " +
            "JOIN b.showtime s " +
            "JOIN s.movie m " +
            "WHERE b.status = 'PAID' " +
            "GROUP BY m.title " +
            "ORDER BY SUM(b.price) DESC")
    List<Object[]> getRevenueByMovie();

    // 6. Đếm TỔNG số vé toàn hệ thống theo trạng thái (PAID)
    long countByStatus(String status);

    // 7. BACKGROUND JOB: Tìm các vé đang "CHỜ THANH TOÁN" (PENDING) đã vượt quá thời gian cho phép
    List<Booking> findByStatusAndBookingDateBefore(String status, LocalDateTime cutOffTime);
}