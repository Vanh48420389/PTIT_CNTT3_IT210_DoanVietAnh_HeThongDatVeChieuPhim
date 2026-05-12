package va.edu.rikkei.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import va.edu.rikkei.model.entity.User;
import va.edu.rikkei.repository.UserRepository;
import va.edu.rikkei.service.UserService;
import va.edu.rikkei.util.SecurityUtil; // Thêm import này

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User registerUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email này đã được đăng ký!");
        }

        // BĂM MẬT KHẨU TRƯỚC KHI LƯU VÀO DATABASE
        String hashedPassword = SecurityUtil.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);

        user.setRole("ROLE_USER");
        return userRepository.save(user);
    }

    @Override
    public User loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        // KHI ĐĂNG NHẬP: Băm mật khẩu người dùng vừa nhập vào...
        String hashedInputPassword = SecurityUtil.hashPassword(password);

        // ...rồi so sánh chuỗi băm đó với chuỗi băm đang lưu trong Database
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(hashedInputPassword)) {
            return userOpt.get();
        }
        return null;
    }

    @Override
    public User updateUser(User updatedUser) {
        // Tìm user cũ trong DB theo ID
        Optional<User> existingUserOpt = userRepository.findById(updatedUser.getId());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            // Chỉ cho phép cập nhật Tên và Số điện thoại (không cho đổi Email hoặc Role ở đây)
            existingUser.setFullName(updatedUser.getFullName());
            existingUser.setPhone(updatedUser.getPhone());

            // Lưu lại vào DB
            return userRepository.save(existingUser);
        }
        return null;
    }
}