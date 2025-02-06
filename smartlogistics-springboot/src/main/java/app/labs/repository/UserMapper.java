package app.labs.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import app.labs.model.Users;

@Mapper
public interface UserMapper {
	
	@Select("SELECT * FROM users WHERE username = #{username}")
    Users findByUsername(String username);
}