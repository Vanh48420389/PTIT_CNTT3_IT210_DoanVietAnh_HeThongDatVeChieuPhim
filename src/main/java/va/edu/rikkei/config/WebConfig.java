package va.edu.rikkei.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import va.edu.rikkei.interceptor.AuthInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                // Áp dụng bộ đánh chặn cho các đường dẫn bắt đầu bằng /admin/ hoặc /user/ hoặc /booking/
                .addPathPatterns("/admin/**", "/booking/**", "/profile/**")

                // Loại trừ các đường dẫn công khai (không cần đăng nhập vẫn xem được)
                .excludePathPatterns("/", "/login", "/register", "/css/**", "/js/**", "/images/**");
    }
}