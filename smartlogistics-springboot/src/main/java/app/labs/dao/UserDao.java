package app.labs.dao;

import org.apache.ibatis.annotations.Mapper;

import app.labs.model.Users;

@Mapper
public interface UserDao {
	
	Users findByUsername(String username);
}