package va.edu.rikkei.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.edu.rikkei.model.entity.Booking;
import va.edu.rikkei.model.entity.Seat;
import va.edu.rikkei.model.entity.Showtime;
import va.edu.rikkei.model.entity.User;
import va.edu.rikkei.repository.BookingRepository;
import va.edu.rikkei.repository.SeatRepository;
import va.edu.rikkei.repository.ShowtimeRepository;
import va.edu.rikkei.repository.UserRepository;
import va.edu.rikkei.service.BookingService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    // ĐÃ SỬA: Đổi void thành Booking
    public Booking bookTicket(Long userId, Long showtimeId, Long seatId, double price) throws Exception {

        // 1. CHỐNG TRANH GHẾ (CORE-09 UPDATE: Bỏ qua những vé đã bị CANCELLED)
        if (bookingRepository.existsByShowtimeIdAndSeatIdAndStatus(showtimeId, seatId, "PAID")) {
            throw new Exception("Ghế này đã có người đặt! Vui lòng chọn ghế khác.");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new Exception("Không tìm thấy User"));
        Showtime showtime = showtimeRepository.findById(showtimeId).orElseThrow(() -> new Exception("Không tìm thấy Suất chiếu"));
        Seat seat = seatRepository.findById(seatId).orElseThrow(() -> new Exception("Không tìm thấy Ghế"));

        Booking newBooking = new Booking();
        newBooking.setUser(user);
        newBooking.setShowtime(showtime);
        newBooking.setSeat(seat);
        newBooking.setPrice(price);
        newBooking.setBookingDate(LocalDateTime.now());

        // Trạng thái mặc định khi mua thành công
        newBooking.setStatus("PAID");

        // ĐÃ SỬA: Lưu vào DB và return luôn đối tượng đó
        return bookingRepository.save(newBooking);
    }

    // 2. CORE-09: HÀM HỦY VÉ (Chủ động kiểm tra luật 24h)
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelTicket(Long bookingId, Long userId) throws Exception {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new Exception("Không tìm thấy vé!"));

        // Bảo mật: Vé của ai người nấy hủy
        if (!booking.getUser().getId().equals(userId)) {
            throw new Exception("Bạn không có quyền hủy vé của người khác!");
        }

        // Chống hủy 2 lần
        if ("CANCELLED".equals(booking.getStatus())) {
            throw new Exception("Vé này đã được hủy trước đó rồi!");
        }

        // Kiểm tra luật 24h
        LocalDateTime showtimeStart = booking.getShowtime().getStartTime();
        LocalDateTime deadlineToCancel = LocalDateTime.now().plusHours(24);

        if (showtimeStart.isBefore(deadlineToCancel)) {
            throw new Exception("Chỉ được hủy vé trước giờ chiếu ít nhất 24 tiếng!");
        }

        // Đổi trạng thái -> Lập tức ghế này sẽ được hàm bookTicket ở trên "tha mạng" (giải phóng)
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }

    // ====================================================
    // 3. QUYỀN LỰC TỐI THƯỢNG CỦA ADMIN: Hủy mọi loại vé, bỏ qua luật 24h
    // ====================================================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelTicketByAdmin(Long bookingId) throws Exception {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new Exception("Không tìm thấy vé!"));

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new Exception("Vé này đã được hủy từ trước rồi!");
        }

        // Đổi trạng thái -> Giải phóng ghế ngay lập tức
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }
}