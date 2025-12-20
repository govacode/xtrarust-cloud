package com.xtrarust.cloud.auth.util;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Slf4j
public class TotpUtil {

    private static final long TIME_STEP_IN_SECONDS = 300;

    private static final int PASSWORD_LENGTH = 6;

    TimeBasedOneTimePasswordGenerator totp;

    KeyGenerator keyGenerator;

    {
        try {
            totp = new TimeBasedOneTimePasswordGenerator(Duration.ofSeconds(TIME_STEP_IN_SECONDS), PASSWORD_LENGTH);
            keyGenerator = KeyGenerator.getInstance(totp.getAlgorithm());
            // Key length should match the length of the HMAC output (160 bits for SHA-1, 256 bits
            // for SHA-256, and 512 bits for SHA-512). Note that while Mac#getMacLength() returns a
            // length in _bytes,_ KeyGenerator#init(int) takes a key length in _bits._
            final int macLengthInBytes = Mac.getInstance(totp.getAlgorithm()).getMacLength();
            keyGenerator.init(macLengthInBytes * 8);
        } catch (NoSuchAlgorithmException e) {
            log.error("construct otp error", e);
        }
    }

    public TotpUtil() {
    }

    public String genOtp(String encodedKey) throws InvalidKeyException {
        return totp.generateOneTimePasswordString(decodeKey(encodedKey), Instant.now());
    }

    public boolean validateOtp(String encodedKey, String otp) throws InvalidKeyException {
        return encodedKey != null && genOtp(encodedKey).equals(otp);
    }

    public String encodeKey() {
        return Base64.getEncoder().encodeToString(keyGenerator.generateKey().getEncoded());
    }

    public Key decodeKey(String encodedKey) {
        return new SecretKeySpec(Base64.getDecoder().decode(encodedKey), totp.getAlgorithm());
    }

    public long getTimeStepInSeconds() {
        return TIME_STEP_IN_SECONDS;
    }
}
