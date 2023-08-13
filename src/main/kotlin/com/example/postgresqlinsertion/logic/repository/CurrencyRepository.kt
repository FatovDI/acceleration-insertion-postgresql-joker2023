package com.example.postgresqlinsertion.logic.repository

import com.example.postgresqlinsertion.logic.entity.CurrencyEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CurrencyRepository : JpaRepository<CurrencyEntity, Long> {
}
