package com.example.postgresqlinsertion.repository

import com.example.postgresqlinsertion.entity.CurrencyEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CurrencyRepository : JpaRepository<CurrencyEntity, Long> {
}
