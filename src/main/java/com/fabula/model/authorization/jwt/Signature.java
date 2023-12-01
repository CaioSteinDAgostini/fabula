/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.model.authorization.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
//import java.util.Base64;
import java.util.HashMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.tomcat.util.codec.binary.Base64;

//import io.jsonwebtoken.SignatureAlgorithm;    
//import io.jsonwebtoken.Jwts;
/**
 *
 * @author caio
 */
public class Signature extends HashMap<String, Object> implements JwtComponent {

    private final Jwt jwt;
    private final String encoded;

    public Signature(Jwt jwt, String secretKey) throws InvalidKeyException, IOException {
        this.jwt = jwt;
        this.encoded = sign(secretKey);
    }

    public Signature(String encoded, Jwt jwt) {
        this.jwt = jwt;
        this.encoded = encoded;
    }

    static byte[] calcHmacSha256(byte[] secretKey, byte[] message) throws InvalidKeyException {
        byte[] hmacSha256 = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
            mac.init(secretKeySpec);

            hmacSha256 = mac.doFinal(message);
        } catch (InvalidKeyException e) {
            throw new InvalidKeyException();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException();
        }
        return hmacSha256;
    }

    private String sign(String secretKey) throws InvalidKeyException, IOException {

        String data = jwt.getHeader().encode() + "." + jwt.getPayload().encode();

        byte[] base64 = calcHmacSha256(secretKey.getBytes(), data.getBytes());
        return Base64.encodeBase64URLSafeString(base64);
    }

    @Override
    public String encode() {
        return encoded;
    }

    public Jwt getJwt() {
        return jwt;
    }

    public String getEncoded() {
        return encoded;
    }

    public static String createSecretKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] secretBytes = new byte[36]; //36*8=288 (>256 bits required for HS256)
        secureRandom.nextBytes(secretBytes);
        java.util.Base64.Encoder encoder = java.util.Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(secretBytes);
    }

}
