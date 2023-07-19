package com.example.postgresqlinsertion.entity

import java.io.Serializable
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.SequenceGenerator

@MappedSuperclass
abstract class BaseEntity : Serializable {

    @Id
    @SequenceGenerator(name = "seq_gen", sequenceName = "seq_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_gen")
    var id: Long? = null

}
