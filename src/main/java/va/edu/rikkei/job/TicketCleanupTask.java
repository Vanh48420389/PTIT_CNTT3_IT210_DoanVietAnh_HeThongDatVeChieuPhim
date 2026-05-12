package va.edu.rikkei.job;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import va.edu.rikkei.model.entity.Booking;
import va.edu.rikkei.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TicketCleanupTask {

    private final BookingRepository bookingRepository;

    // Chạy ngầm định kỳ mỗi 60.000 milliseconds (1 phút)
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cleanupExpiredBookings() {
        // Lấy mốc thời gian: Hiện tại lùi lại 15 phút
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(15);

        // Quét DB tìm những vé PENDING mà được tạo trước mốc 15 phút đó
        List<Booking> expiredBookings = bookingRepository.findByStatusAndBookingDateBefore("PENDING", cutoffTime);

        if (!expiredBookings.isEmpty()) {
            for (Booking b : expiredBookings) {
                b.setStatus("CANCELLED");
                bookingRepository.save(b);
                System.out.println("[CRON JOB] Đã hủy tự động vé #" + b.getId() + " do quá 15 phút chưa thanh toán.");
            }
        }
    }
}