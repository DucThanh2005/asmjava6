package com.example.carstore.service;

import com.example.carstore.entity.Account;
import com.example.carstore.repository.AccountRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AccountRepository accountRepo;

    public CustomOAuth2UserService(AccountRepository accountRepo) {
        this.accountRepo = accountRepo;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User googleUser = super.loadUser(request);

        String email = googleUser.getAttribute("email");
        String name = googleUser.getAttribute("name");

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Google account does not provide email");
        }

        Account account = accountRepo.findByEmail(email);
        if (account == null) {
            account = createOAuthAccount(email, name);
        } else {
            boolean changed = false;
            if (account.getUsername() == null || account.getUsername().trim().isEmpty()) {
                account.setUsername(email);
                changed = true;
            }
            if ((account.getFullname() == null || account.getFullname().trim().isEmpty())
                    && name != null && !name.trim().isEmpty()) {
                account.setFullname(name);
                changed = true;
            }
            if (account.getRole() == null || account.getRole().trim().isEmpty()) {
                account.setRole("ROLE_USER");
                changed = true;
            }
            if (changed) {
                accountRepo.save(account);
            }
        }

        Set<SimpleGrantedAuthority> authorities = new LinkedHashSet<>();
        authorities.add(new SimpleGrantedAuthority(account.getRole()));
        googleUser.getAuthorities().forEach(a -> authorities.add(new SimpleGrantedAuthority(a.getAuthority())));

        // Quan trọng: đặt nameAttributeKey = "email" để auth.getName() trả về email,
        // khớp với username trong bảng account. Nếu không, Google thường trả về "sub"
        // khiến profile/order/history không tìm thấy user trong database.
        return new DefaultOAuth2User(authorities, googleUser.getAttributes(), "email");
    }

    private Account createOAuthAccount(String email, String name) {
        Account account = new Account();
        account.setEmail(email);
        account.setUsername(email);
        account.setFullname(name == null || name.trim().isEmpty() ? email : name);
        account.setPassword("{noop}oauth2");
        account.setRole("ROLE_USER");
        return accountRepo.save(account);
    }
}
