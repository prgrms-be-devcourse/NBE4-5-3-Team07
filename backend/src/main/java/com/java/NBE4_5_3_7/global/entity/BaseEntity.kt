package com.java.NBE4_5_3_7.global.entity

import jakarta.persistence.*

@MappedSuperclass
abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private var _id: Long? = null // TODO: 추후에 코틀린 전환 과정에서 해결

    var id: Long
        get() = _id ?: 0
        set(value) {
            _id = value
        }
}