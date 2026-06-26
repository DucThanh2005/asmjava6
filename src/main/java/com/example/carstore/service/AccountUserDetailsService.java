package com.example.carstore.service;

import com.example.carstore.entity.Account;
import com.example.carstore.repository.AccountRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AccountUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    public AccountUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return User.builder()
                .username(account.getUsername())
                .password(account.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(resolveRole(account))))
                .build();
    }

    private String resolveRole(Account account) {
        return account.getRole() == null || account.getRole().trim().isEmpty()
                ? "ROLE_USER"
                : account.getRole();
    }
}
