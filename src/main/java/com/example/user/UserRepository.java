package com.example.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
   
    //optional java util
    //no necesitamos consultas pq las q necesitamos ya estan aqui jpa
    //si quisieramos relacionar dos tablas entonces si que tendriamos q usar queries 
    Optional<User> findByEmail(String email);
    void deleteByEmail(String email);

}
