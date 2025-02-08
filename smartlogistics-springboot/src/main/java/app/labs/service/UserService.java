package app.labs.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import app.labs.dao.UserDao;
import app.labs.model.Users;

@Service
public class UserService implements UserDetailsService{

	private final UserDao userDao;

    public UserService(UserDao userMapper) {
        this.userDao = userMapper;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userDao.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return User.builder()
            .username(user.getUsername())
            .password(user.getPassword())  // 암호화된 비밀번호 사용
            .roles(user.getRole()) // DB에 저장된 역할 적용
            .build();
    }
    
}
