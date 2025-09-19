package com.bankingsystem.bankingsystem.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Customer {

    public enum Role { ADMIN, CUSTOMER }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String address;
    private String password;

    @Convert(converter = RoleConverter.class)
    private Role role;

    private Integer creditScore = 600; // default
}
