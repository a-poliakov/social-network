package ru.apolyakov.generator.providers;

import com.presidentio.testdatagenerator.context.Context;
import com.presidentio.testdatagenerator.model.Field;
import com.presidentio.testdatagenerator.provider.ConstValueProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordProvider extends ConstValueProvider {
    public static final String PASSWORD_PROVIDER_NAME  = "password-provider";
    public static final String DEFAULT_PASSWORD = "123";

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Object nextValue(Context context, Field field) {
        return passwordEncoder.encode(DEFAULT_PASSWORD);
    }
}
