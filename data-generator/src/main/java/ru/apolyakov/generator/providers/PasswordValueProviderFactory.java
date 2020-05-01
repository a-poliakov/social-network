package ru.apolyakov.generator.providers;

import com.presidentio.testdatagenerator.model.Provider;
import com.presidentio.testdatagenerator.provider.ValueProvider;
import com.presidentio.testdatagenerator.provider.ValueProviderFactory;
import org.springframework.stereotype.Component;

@Component
public class PasswordValueProviderFactory implements ValueProviderFactory {

    public ValueProvider buildValueProvider(Provider provider) {
        if (PasswordProvider.PASSWORD_PROVIDER_NAME.equals(provider.getName())) {
            return new PasswordProvider();
        }
        return null;
    }
}
