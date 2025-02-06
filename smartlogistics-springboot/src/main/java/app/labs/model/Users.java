package app.labs.model;


import lombok.Data;

@Data
public class Users {
    private String username;
    private String password;
    private String role;
}