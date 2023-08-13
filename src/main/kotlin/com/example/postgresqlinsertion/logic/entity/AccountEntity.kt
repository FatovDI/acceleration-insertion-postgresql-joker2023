package com.example.postgresqlinsertion.logic.entity

import javax.persistence.*

@Entity
@Table(name = "account")
class AccountEntity(
    var number: String? = null,
    var name: String? = null,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cur", referencedColumnName = "code")
    var cur: CurrencyEntity? = null,
) : BaseEntity()
