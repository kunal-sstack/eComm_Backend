package com.projectX.backend.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
}
