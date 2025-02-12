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
        
    	System.out.println("로그인 시도: " + username);
    	
    	Users user = userDao.findByUsername(username);
        if (user == null) {
        	System.out.println("로그인 실패: 사용자 없음");
            throw new UsernameNotFoundException("User not found: " + username);
        }
        
        System.out.println("로그인 성공: " + username + ", 권한: " + user.getRole());        
        
        // 비밀번호가 암호화되어 있음
        System.out.println("암호화된 비밀번호: " + user.getPassword());

        return User.builder()
            .username(user.getUsername())
            .password(user.getPassword())  // 암호화된 비밀번호 사용
            .roles(user.getRole().toUpperCase()) // ROLE_ADMIN, ROLE_WORKER 설정
            .build();
    }
    
    /*
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
    */
    
}
