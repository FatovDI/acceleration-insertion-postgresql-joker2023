package com.example.postgresqlinsertion.logic.entity

import java.io.Serializable
import javax.persistence.*

@MappedSuperclass
abstract class BaseEntity : Serializable {

    @Id
    @SequenceGenerator(name = "seq_gen", sequenceName = "seq_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_gen")
    var id: Long? = null

}
