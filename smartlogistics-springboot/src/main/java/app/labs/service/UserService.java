package app.labs.service;

import org.springframework.stereotype.Service;

import app.labs.model.Users;
import app.labs.repository.UserMapper;

@Service
public class UserService {

	private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public Users findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
}
