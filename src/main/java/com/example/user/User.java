package com.example.user;

import org.hibernate.annotations.NaturalId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//Le ponemos un nombre para no confundirnos con otra tabla que ya existe que se llama users
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)    
    private long id;
    private String firstName;
    private String lastName;

    @Column(unique = true) //con esto el campo no se repite(el email), pero no forma parte de la PK
    //@NaturalId(mutable = true) Para que forme parte de la PK 
    private String email;

    private String password; 

    //Rol que vamos a asignar al usuario
    @Enumerated(EnumType.STRING)
    private Role role; 
}
