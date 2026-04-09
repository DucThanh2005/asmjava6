package com.example.carstore.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
// SỬA DÒNG DƯỚI ĐÂY:
import org.springframework.security.oauth2.core.user.OAuth2User; 
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.carstore.entity.Account;
import com.example.carstore.repository.AccountRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private AccountRepository accountRepo;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        // Phương thức super.loadUser(request) sẽ gọi đến Google/Facebook để lấy thông tin
        OAuth2User user = super.loadUser(request);

        String email = user.getAttribute("email");
        String name = user.getAttribute("name");

        Account acc = accountRepo.findByEmail(email);

        if (acc == null) {
            Account newAcc = new Account();
            newAcc.setEmail(email);
            newAcc.setUsername(email); // Nên dùng email làm username để tránh trùng
            newAcc.setFullname(name);  // Đảm bảo set fullname từ Google
            newAcc.setPassword(""); 
           
            // GÁN ROLE USER
            newAcc.setRole("ROLE_USER");

            accountRepo.save(newAcc);
        }

        return user;
    }
}