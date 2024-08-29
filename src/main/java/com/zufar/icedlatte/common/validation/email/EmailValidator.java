package com.zufar.icedlatte.common.validation.email;

import com.zufar.icedlatte.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EmailValidator {

    private static final String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    private final UserRepository userCrudRepository;

    public boolean isNotValid(String email) {
        return email == null || !email.matches(emailRegex);
    }

    public boolean isEmailUnique(String email) {
        return userCrudRepository
                .findByEmail(email)
                .isEmpty();
    }
}
