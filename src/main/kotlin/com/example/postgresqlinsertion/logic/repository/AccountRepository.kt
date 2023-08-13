package com.example.postgresqlinsertion.logic.repository

import com.example.postgresqlinsertion.logic.entity.AccountEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : JpaRepository<AccountEntity, Long> {
}
