package com.tdea.proyecto.repository;

import com.tdea.proyecto.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provides persistence operations for bank accounts.
 */
public interface AccountRepository extends JpaRepository<Account, Long> {
}
