package com.karunamay.airlink.model.user;

import jakarta.persistence.*;

@MappedSuperclass
public class BaseModel {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;
}
