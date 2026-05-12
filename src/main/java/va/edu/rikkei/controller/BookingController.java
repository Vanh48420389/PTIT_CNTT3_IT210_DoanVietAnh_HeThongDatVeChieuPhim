package va.edu.rikkei.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import va.edu.rikkei.model.entity.*;
import va.edu.rikkei.repository.BookingRepository;
import va.edu.rikkei.repository.MovieRepository;
import va.edu.rikkei.repository.SeatRepository;
import va.edu.rikkei.repository.ShowtimeRepository;
import va.edu.rikkei.service.BookingService;
// IMPORT THÊM EMAIL SERVICE VÀO ĐÂY:
import va.edu.rikkei.service.EmailService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {

    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final MovieRepository movieRepository;

    // TIÊM EMAIL SERVICE VÀO ĐỂ SỬ DỤNG
    private final EmailService emailService;

    // 1. HIỂN THỊ SƠ ĐỒ GHẾ CỦA SUẤT CHIẾU
    @GetMapping("/{showtimeId}")
    public String showSeatMap(@PathVariable Long showtimeId, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        Showtime showtime = showtimeRepository.findById(showtimeId).orElse(null);
        if (showtime == null) return "redirect:/";

        List<Seat> allSeats = seatRepository.findByRoomId(showtime.getRoom().getId());

        List<Booking> bookedTickets = bookingRepository.findByShowtimeIdAndStatus(showtimeId, "PAID");

        List<Long> bookedSeatIds = bookedTickets.stream()
                .map(booking -> booking.getSeat().getId())
                .collect(Collectors.toList());

        model.addAttribute("showtime", showtime);
        model.addAttribute("allSeats", allSeats);
        model.addAttribute("bookedSeatIds", bookedSeatIds);

        return "seat-selection";
    }

    // 2. XỬ LÝ NÚT "THANH TOÁN" (TÍCH HỢP GỬI EMAIL QR NGẦM)
    @PostMapping("/{showtimeId}/checkout")
    public String processCheckout(@PathVariable Long showtimeId,
                                  @RequestParam("seatId") Long seatId,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        try {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new Exception("Ghế không tồn tại!"));

            double price = seat.getType().equalsIgnoreCase("VIP") ? 120000.0 : 85000.0;

            // Gọi hàm Transactional để chốt đơn và HỨNG lấy vé vừa tạo
            Booking newBooking = bookingService.bookTicket(loggedInUser.getId(), showtimeId, seatId, price);

            // ==========================================
            // KÍCH HOẠT TIẾN TRÌNH GỬI EMAIL CHẠY NGẦM
            // Hàm này có @Async nên sẽ tự động tách ra 1 luồng riêng,
            // không bắt Khách hàng phải chờ web load xong email.
            // ==========================================
            emailService.sendTicketEmail(newBooking);

            redirectAttributes.addFlashAttribute("successMsg", "🎉 Đặt vé thành công! Mã QR đã được gửi vào Email của bạn.");
            return "redirect:/booking/" + showtimeId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "⚠️ " + e.getMessage());
            return "redirect:/booking/" + showtimeId;
        }
    }

    // 3. HIỂN THỊ DANH SÁCH SUẤT CHIẾU
    @GetMapping("/movie/{movieId}/showtimes")
    public String showMovieShowtimes(@PathVariable Long movieId, Model model) {
        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie == null) return "redirect:/";

        LocalDateTime now = LocalDateTime.now();
        List<Showtime> showtimes = showtimeRepository.findByMovieIdAndStartTimeAfterOrderByStartTimeAsc(movieId, now);

        Map<Long, Boolean> soldOutMap = new HashMap<>();

        for (Showtime st : showtimes) {
            long totalSeats = seatRepository.countByRoomId(st.getRoom().getId());
            long bookedTickets = bookingRepository.countByShowtimeIdAndStatus(st.getId(), "PAID");
            soldOutMap.put(st.getId(), bookedTickets >= totalSeats);
        }

        model.addAttribute("movie", movie);
        model.addAttribute("showtimes", showtimes);
        model.addAttribute("soldOutMap", soldOutMap);

        return "movie-showtimes";
    }

    // 4. CORE-07: TRA CỨU LỊCH SỬ ĐẶT VÉ
    @GetMapping("/history")
    public String showBookingHistory(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<Booking> bookings = bookingRepository.findByUserIdOrderByBookingDateDesc(loggedInUser.getId());

        model.addAttribute("bookings", bookings);
        return "booking-history";
    }

    // 5. CORE-09: XỬ LÝ HỦY VÉ TỪ GIAO DIỆN LỊCH SỬ
    @PostMapping("/cancel/{bookingId}")
    public String cancelBooking(@PathVariable Long bookingId, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        try {
            bookingService.cancelTicket(bookingId, loggedInUser.getId());
            redirectAttributes.addFlashAttribute("successMsg", "Hủy vé thành công! Tiền sẽ được hoàn lại và ghế đã được giải phóng.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "⚠️ " + e.getMessage());
        }

        return "redirect:/booking/history";
    }
}