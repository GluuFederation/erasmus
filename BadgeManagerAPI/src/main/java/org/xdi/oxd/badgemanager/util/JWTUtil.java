package org.xdi.oxd.badgemanager.util;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Arvind Tomar on 21/4/17.
 */
@Component
public class JWTUtil {

    private static final Logger logger = LoggerFactory.getLogger(JWTUtil.class);

    /**
     * Generates JWT using private key.
     */
    public String generateJWTWithPrivateKey(String payload) {

        try {
            // RSA signatures require a public and private RSA key pair, the public key
            // must be made known to the JWS recipient in order to verify the signatures
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
            keyGenerator.initialize(1024);

            KeyPair kp = keyGenerator.genKeyPair();
            RSAPrivateKey privateKey = (RSAPrivateKey) kp.getPrivate();

            // Create RSA-signer with the public key
            JWSSigner signer = new RSASSASigner(privateKey);

            // Prepare JWT with claims set
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject("test")
                    .issuer("http://www.gluu.org")
                    .claim("userinfo", payload)
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.RS256),
                    claimsSet);

            // Compute the RSA signature
            signedJWT.sign(signer);

            // To serialize to compact form, produces something like
            // eyJhbGciOiJSUzI1NiJ9.SW4gUlNBIHdlIHRydXN0IQ.IRMQENi4nJyp4er2L
            // mZq3ivwoAjqa1uUkSBKFIX7ATndFF5ivnt-m8uApHO4kfIFOrW7w2Ezmlg3Qd
            // maXlS9DhN0nUk_hGI3amEjkKd0BWYCB8vfUbUv0XGjQip78AI4z1PrFRNidm7
            // -jPDm5Iq0SZnjKjCNS5Q15fokXZc8u0A
            return signedJWT.serialize();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception in generating JWT: " + ex.getMessage());
        }
        return null;
    }

    /**
     * Generates JWT.
     */
    public String generateJWT(String payload) {

        try {

//            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
//            keyGenerator.initialize(1024);
//
//            KeyPair kp = keyGenerator.genKeyPair();
//            RSAPublicKey publicKey = (RSAPublicKey) kp.getPublic();

            String publicKey="-----BEGIN PUBLIC KEY-----\n" +
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA33TqqLR3eeUmDtHS89qF\n" +
                    "3p4MP7Wfqt2Zjj3lZjLjjCGDvwr9cJNlNDiuKboODgUiT4ZdPWbOiMAfDcDzlOxA\n" +
                    "04DDnEFGAf+kDQiNSe2ZtqC7bnIc8+KSG/qOGQIVaay4Ucr6ovDkykO5Hxn7OU7s\n" +
                    "Jp9TP9H0JH8zMQA6YzijYH9LsupTerrY3U6zyihVEDXXOv08vBHk50BMFJbE9iwF\n" +
                    "wnxCsU5+UZUZYw87Uu0n4LPFS9BT8tUIvAfnRXIEWCha3KbFWmdZQZlyrFw0buUE\n" +
                    "f0YN3/Q0auBkdbDR/ES2PbgKTJdkjc/rEeM0TxvOUf7HuUNOhrtAVEN1D5uuxE1W\n" +
                    "SwIDAQAB\n" +
                    "-----END PUBLIC KEY-----";

            return Jwts.builder()
                    .setId(UUID.randomUUID().toString())
                    .setSubject("test")
                    .setIssuer("http://www.gluu.org")
                    .claim("userinfo", payload)
                    .setIssuedAt(new Date())
                    .signWith(SignatureAlgorithm.HS512, publicKey)
                    .compact();

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception in generating JWT: " + ex.getMessage());
        }
        return null;
    }
}
