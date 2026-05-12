package va.edu.rikkei.service;

import va.edu.rikkei.model.entity.User;

public interface UserService {
    User registerUser(User user);
    User loginUser(String email, String password);
    User updateUser(User user);
}