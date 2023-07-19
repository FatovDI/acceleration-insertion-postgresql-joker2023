package com.example.postgresqlinsertion.entity

import org.hibernate.annotations.NaturalId
import org.hibernate.annotations.NaturalIdCache
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "currency")
@NaturalIdCache
class CurrencyEntity(
    @NaturalId(mutable = true)
    var code: String? = null,
    var name: String? = null,
) : BaseEntity()
