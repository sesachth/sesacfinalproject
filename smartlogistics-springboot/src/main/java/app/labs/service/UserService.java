package app.labs.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
        	System.out.println("로그인 실패: 사용자 없음");
            throw new UsernameNotFoundException("User not found: " + username);
        }  
             
        return User.builder()
            .username(user.getUsername())
            .password(user.getPassword())  // 암호화된 비밀번호 사용
            .roles(user.getRole().toUpperCase()) // ROLE_ADMIN, ROLE_WORKER 설정
            .build();
    }
    
}
