package ru.apolyakov.social_network.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    private String login;
    private String password;

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "second_name")
    private String secondName;

    private int age;

    @Column(name = "sex")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String interests;
    private String city;


}
