package va.edu.rikkei.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ràng buộc Tên không được để trống
    @NotBlank(message = "Họ và tên không được để trống")
    @Column(nullable = false)
    private String fullName;

    // Ràng buộc định dạng Email bằng Regex
    @NotBlank(message = "Email không được để trống")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
            message = "Email không đúng định dạng (VD: abc@gmail.com)")
    @Column(nullable = false, unique = true)
    private String email;

    // Ràng buộc Mật khẩu
    @NotBlank(message = "Mật khẩu không được để trống")
    @Column(nullable = false)
    private String password;

    // Ràng buộc định dạng Số điện thoại Việt Nam bằng Regex (10 số, bắt đầu bằng 0 hoặc +84)
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$",
            message = "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0 hoặc +84")
    @Column(nullable = false) // Nên thêm nullable = false nếu db bắt buộc có sđt
    private String phone;

    private String role; // ROLE_USER hoặc ROLE_ADMIN
}