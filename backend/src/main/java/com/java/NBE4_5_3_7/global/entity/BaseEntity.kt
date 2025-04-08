package com.java.NBE4_5_3_7.global.entity

import jakarta.persistence.*

@MappedSuperclass
abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private var _id: Long? = null

    var id: Long
        get() = _id ?: 0
        set(value) {
            _id = value
        }
}