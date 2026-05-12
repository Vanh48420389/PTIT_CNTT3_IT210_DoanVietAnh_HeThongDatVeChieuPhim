package va.edu.rikkei.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import va.edu.rikkei.model.entity.User;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loggedInUser");
        String requestURI = request.getRequestURI();

        // 1. Nếu chưa đăng nhập mà đòi vào các trang cần quyền bảo mật
        if (user == null) {
            // Chuyển hướng về trang đăng nhập
            response.sendRedirect("/login");
            return false; // Chặn request đi tiếp
        }

        // 2. Nếu đã đăng nhập nhưng lại muốn vào vùng của Admin (/admin/...)
        if (requestURI.startsWith("/admin")) {
            // Kiểm tra xem Role có phải là Admin không
            if (!"ROLE_ADMIN".equals(user.getRole())) {
                // Báo lỗi 403: Forbidden (Không có quyền truy cập)
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang quản trị này!");
                return false; // Chặn request
            }
        }

        // Hợp lệ thì cho phép đi tiếp đến Controller
        return true;
    }
}