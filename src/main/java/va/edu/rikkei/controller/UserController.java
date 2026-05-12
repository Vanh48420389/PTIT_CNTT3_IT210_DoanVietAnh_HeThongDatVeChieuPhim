package va.edu.rikkei.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import va.edu.rikkei.model.entity.User;
import va.edu.rikkei.service.UserService;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ================= ĐĂNG KÝ (CORE-01 + VALIDATE) =================
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("user") User user,
                                  BindingResult bindingResult,
                                  Model model) {
        // 1. Kiểm tra Validate (để trống, sai định dạng email/sđt, pass quá ngắn...)
        if (bindingResult.hasErrors()) {
            return "register"; // Có lỗi -> Trả lại form để hiện chữ đỏ
        }

        // 2. Nếu vượt qua Validate, tiến hành lưu vào Database
        try {
            userService.registerUser(user);
            return "redirect:/login?registered=true"; // Thành công -> Chuyển sang trang Login
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage()); // Báo lỗi nếu trùng Email
            return "register";
        }
    }

    // ================= ĐĂNG NHẬP (CORE-01 + CORE-02) =================
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {

        // Kiểm tra xem khách có bỏ trống ô nào không
        if (email.trim().isEmpty() || password.trim().isEmpty()) {
            model.addAttribute("error", "Vui lòng nhập đầy đủ Email và Mật khẩu!");
            return "login";
        }

        // Xác thực tài khoản với Database
        User user = userService.loginUser(email, password);
        if (user != null) {
            // Lưu phiên đăng nhập
            session.setAttribute("loggedInUser", user);

            // Điều hướng theo Role
            if ("ROLE_ADMIN".equals(user.getRole())) {
                return "redirect:/admin/dashboard";
            }
            return "redirect:/"; // Nếu là User thường thì về Trang chủ
        }

        model.addAttribute("error", "Email hoặc mật khẩu không chính xác!");
        return "login";
    }

    // ================= ĐĂNG XUẤT =================
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("loggedInUser"); // Xóa phiên làm việc
        return "redirect:/";
    }

    // ================= HỒ SƠ CÁ NHÂN (CORE-03) =================
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Bắt buộc đăng nhập
        }

        // Đẩy thông tin lên form
        model.addAttribute("user", loggedInUser);
        return "profile";
    }

    @PostMapping("/profile")
    public String processUpdateProfile(@ModelAttribute("user") User updatedUser, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            // Gắn lại ID cũ vào object gửi lên để update đúng người
            updatedUser.setId(loggedInUser.getId());

            // Lưu cập nhật vào DB
            User savedUser = userService.updateUser(updatedUser);

            // Cập nhật thông tin mới vào ngay Session để đổi tên trên thanh Menu
            session.setAttribute("loggedInUser", savedUser);
        }
        return "redirect:/profile?success=true";
    }
}