package com.example.demo.domain.user.service;

import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.exception.EmailCodeOrEmailMissMatchException;
import com.example.demo.domain.user.exception.PasswordMissMatchException;
import com.example.demo.domain.user.facade.UserFacade;
import com.example.demo.domain.user.presentation.dto.request.FindPasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FindPasswordService {

    private final PasswordEncoder passwordEncoder;
    private final UserFacade userFacade;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public void findPassword(FindPasswordRequest findPasswordRequest) {

        User user = userFacade.getUserByAccountId(findPasswordRequest.getEmail());

        if (findPasswordRequest.getEmailCode().equals(redisTemplate.opsForValue().get(findPasswordRequest.getEmail()))) {
            throw EmailCodeOrEmailMissMatchException.EXCEPTION;
        }

        if (findPasswordRequest.getPassword().equals(findPasswordRequest.getPasswordValid())) {
            throw PasswordMissMatchException.EXCEPTION;
        }

        user.modifyPassword(passwordEncoder.encode(findPasswordRequest.getPassword()));

    }
}
