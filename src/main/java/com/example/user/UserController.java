package com.example.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*; //el asterisco es si vas a usar muchas cosas
//de ese tipo de import pues en vez de ir uno a uno, asi

import java.util.List;

//contraseña: 3797ec3d-0745-4601-8d27-ec39050f26f4
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers(){
        return new ResponseEntity<>(userService.findAll(), HttpStatus.FOUND);
    }
    @PostMapping("/add")
    @Transactional
    public ResponseEntity<User> add(@RequestBody User user){
        return ResponseEntity.ok(userService.add(user));
    }

    @GetMapping("/{email}")
    public User getByEmail(@PathVariable("email") String email){
        return  userService.findByEmail(email);
    }

    @DeleteMapping("/{email}")
    @Transactional
    public void delete(@PathVariable("email") String email){
        userService.deleteByEmail(email);
    }

    @PutMapping("/update")
    @Transactional
    public ResponseEntity<User> update(@RequestBody User user){

        return ResponseEntity.ok(userService.update(user));
    }

}
