package va.edu.rikkei.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import va.edu.rikkei.service.ShowtimeService;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    // ĐÃ XÓA hàm @GetMapping("/add") ở đây để nhường sân khấu cho AdminController.
    // Xóa xong là hết báo lỗi đỏ chót ngay!

    // Xử lý logic khi bấm Tạo suất chiếu
    @PostMapping("/add")
    public String processAddShowtime(@RequestParam("movieId") Long movieId,
                                     @RequestParam("roomId") Long roomId,
                                     @RequestParam("startTime")
                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
                                     RedirectAttributes redirectAttributes) {
        try {
            // Gọi Service để lưu (sẽ tự động check xung đột bên trong)
            showtimeService.createShowtime(movieId, roomId, startTime);
            redirectAttributes.addFlashAttribute("successMsg", "Tạo suất chiếu thành công!");

        } catch (Exception e) {
            // NẾU BỊ XUNG ĐỘT TRÙNG GIỜ -> Bắt lỗi và gửi thông báo đỏ lên màn hình
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        // Xử lý xong, ném Khách về lại trang giao diện của AdminController
        return "redirect:/admin/showtimes/add";
    }
}