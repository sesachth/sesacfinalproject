package app.labs.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import app.labs.model.Users;

@Mapper
public interface UserDao {
    Users findByUsername(@Param("username") String username);
}
