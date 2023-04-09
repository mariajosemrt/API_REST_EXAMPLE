package com.example.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.user.Role;
import com.example.user.User;

import lombok.Data;

@Data

public class MyUserDetails implements UserDetails{

    private String userName;
    private String password;
    //preparandonos para coger los detalles del usuario que ha enctrado en la base de datos
    //a ver si esta autenticado
    private List<GrantedAuthority> authorities;

    public MyUserDetails(User user) {
        this.userName = user.getEmail();
        this.password = user.getPassword();

        //Por cada rol que nos pasa por el stream con el map estamos creando un
        //SimpleGrantedAuthority
        authorities = Arrays
                    .stream(Role.values().toString().split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()); 

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return this.authorities;
    }

    @Override
    public String getPassword() {

       return this.password;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    
}
