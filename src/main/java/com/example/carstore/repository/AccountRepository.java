package com.example.carstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.carstore.entity.Account;

public interface AccountRepository extends JpaRepository<Account, String> {
}

