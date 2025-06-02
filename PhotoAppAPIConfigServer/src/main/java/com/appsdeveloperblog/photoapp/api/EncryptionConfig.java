/*package com.appsdeveloperblog.photoapp.api;
import org.springframework.cloud.config.server.encryption.TextEncryptorLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

@Configuration
public class EncryptionConfig {

    @Bean
    public TextEncryptorLocator textEncryptorLocator() {
        // password and salt must be non-null
        TextEncryptor encryptor = Encryptors.text("fhf73odjsjkhHld98yurH983ndksku48slfhcflfdjG", "1234567890abcdef");
        return (environment) -> encryptor;
    }
}

*/