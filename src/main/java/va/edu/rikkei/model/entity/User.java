package va.edu.rikkei.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    // Ràng buộc định dạng Email
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng (VD: abc@gmail.com)")
    @Column(nullable = false, unique = true)
    private String email;

    // Ràng buộc Mật khẩu
    @NotBlank(message = "Mật khẩu không được để trống")
    @Column(nullable = false)
    private String password;

    // Ràng buộc định dạng Số điện thoại Việt Nam (10 số, bắt đầu bằng 0 hoặc +84)
    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    private String role; // ROLE_USER hoặc ROLE_ADMIN
}