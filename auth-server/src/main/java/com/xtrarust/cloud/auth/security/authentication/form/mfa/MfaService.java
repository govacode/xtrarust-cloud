package com.xtrarust.cloud.auth.security.authentication.form.mfa;

import com.xtrarust.cloud.auth.security.userdetails.MfaUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public interface MfaService {

    String generateMfaId(Authentication authentication);

    void sendOtp(String mfaId, MfaUserDetails mfaUserDetails);

    Authentication validateOtp(String mfaId, String otp) throws AuthenticationException;
}
