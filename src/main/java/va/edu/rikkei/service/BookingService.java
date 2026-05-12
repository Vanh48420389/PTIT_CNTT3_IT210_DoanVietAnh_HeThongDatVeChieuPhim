package va.edu.rikkei.service;

import va.edu.rikkei.model.entity.Booking;

public interface BookingService {

    // ĐÃ SỬA: Trả về đối tượng Booking thay vì void để Controller lấy gửi Email
    Booking bookTicket(Long userId, Long showtimeId, Long seatId, double price) throws Exception;

    // Hàm hủy vé của khách hàng (có kiểm tra luật 24h)
    void cancelTicket(Long bookingId, Long userId) throws Exception;

    // Hàm hủy vé của ADMIN (quyền lực tối thượng, bỏ qua luật 24h)
    void cancelTicketByAdmin(Long bookingId) throws Exception;
}