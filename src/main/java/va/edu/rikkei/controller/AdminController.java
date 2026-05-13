package va.edu.rikkei.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import va.edu.rikkei.model.entity.Booking;
import va.edu.rikkei.model.entity.Showtime;
import va.edu.rikkei.model.entity.User;
import va.edu.rikkei.repository.BookingRepository;
import va.edu.rikkei.repository.MovieRepository;
import va.edu.rikkei.repository.RoomRepository;
import va.edu.rikkei.repository.ShowtimeRepository;
import va.edu.rikkei.service.BookingService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final BookingRepository bookingRepository;
    private final MovieRepository movieRepository;
    private final BookingService bookingService;
    private final RoomRepository roomRepository;
    private final ShowtimeRepository showtimeRepository;

    // ==========================================
    // TỔNG QUAN (DASHBOARD)
    // ==========================================
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null || !loggedInUser.getRole().equals("ROLE_ADMIN")) {
            return "redirect:/";
        }

        long totalMovies = movieRepository.count();
        long totalRooms = roomRepository.count();
        long totalTickets = bookingRepository.countByStatus("PAID");

        List<Object[]> revenueData = bookingRepository.getRevenueByMovie();
        List<String> movieNames = new ArrayList<>();
        List<Double> revenues = new ArrayList<>();
        double totalRevenue = 0;

        for (Object[] row : revenueData) {
            movieNames.add((String) row[0]);
            Double revenue = (Double) row[1];
            revenues.add(revenue);
            totalRevenue += revenue;
        }

        model.addAttribute("totalMovies", totalMovies);
        model.addAttribute("totalRooms", totalRooms);
        model.addAttribute("totalTickets", totalTickets);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("movieNames", movieNames);
        model.addAttribute("revenues", revenues);

        return "admin-dashboard";
    }

    // ==========================================
    // QUẢN LÝ SUẤT CHIẾU (SHOWTIMES)
    // ==========================================
    @GetMapping("/showtimes/add")
    public String showAddShowtimeForm(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size, // Hiển thị 5 suất chiếu 1 trang
            HttpSession session, Model model) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null || !loggedInUser.getRole().equals("ROLE_ADMIN")) return "redirect:/";

        model.addAttribute("movies", movieRepository.findAll());
        model.addAttribute("rooms", roomRepository.findAll());

        // Lấy danh sách lịch chiếu theo Phân trang
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startTime"));
        Page<Showtime> showtimePage = showtimeRepository.findAll(pageable);

        model.addAttribute("showtimePage", showtimePage);

        return "add-showtime";
    }

    @GetMapping("/showtimes/delete/{id}")
    public String deleteShowtime(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null || !loggedInUser.getRole().equals("ROLE_ADMIN")) return "redirect:/";

        try {
            showtimeRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMsg", "Đã xóa suất chiếu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Không thể xóa! Suất chiếu này đã có khách hàng đặt vé.");
        }
        return "redirect:/admin/showtimes/add";
    }

    // ==========================================
    // QUẢN LÝ HÓA ĐƠN (BOOKINGS)
    // ==========================================
    @GetMapping("/bookings")
    public String manageBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            HttpSession session, Model model) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null || !loggedInUser.getRole().equals("ROLE_ADMIN")) {
            return "redirect:/";
        }

        // Lấy danh sách vé theo Phân trang
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "bookingDate"));
        Page<Booking> bookingPage = bookingRepository.findAll(pageable);

        model.addAttribute("bookingPage", bookingPage);
        return "admin-bookings";
    }

    @PostMapping("/bookings/cancel/{id}")
    public String adminCancelBooking(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null || !loggedInUser.getRole().equals("ROLE_ADMIN")) return "redirect:/";

        try {
            bookingService.cancelTicketByAdmin(id);
            redirectAttributes.addFlashAttribute("successMsg", "Đã hủy vé thành công! Ghế đã được giải phóng.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/bookings";
    }
}