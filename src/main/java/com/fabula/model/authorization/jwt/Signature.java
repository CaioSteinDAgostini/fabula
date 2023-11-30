/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.model.authorization.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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

//    public static void main(String[] args) throws InvalidJwtException, InvalidKeyException, JsonProcessingException {
//        String bearer = "eyJ0eXAiOiJqd3QiLCJhbGciOiJIUzI1NiJ9."
//                + "eyJzdWIiOiJyb290QGRvbWFpbi5jb20iLCJkb21haW5zIjp7ImRkYWYyYTczLWQ3MjAtNDNlNC05NmRmLTUzMzk3ZmU2ZGM3MyI6IkZhYnVsYSBQcm9qZWN0IiwiNzVlZDFiNDItZGU0NC00NzgzLThjYTQtZDE0MWNlNTJhMzY3IjoiRmFidWxhIiwiOGEwNzFlNzEtNzk1Zi00ZWFjLWFjNGEtY2MzMzRmYmU1ZDQ3IjoiRmFidWxhIEJsb2cifSwiZXhwIjoxNjQ5MDEzMDIwMDk2LCJpYXQiOjE2NDg5OTUwMjAwOTZ9."
//                + "zU1ip-_TXnlkKN0MNxPug-9pWtOzhw5oDfcoxGmsZCg";
//        String[] parts = bearer.split("\\.");
//        Jwt jwt = new Jwt(bearer);
//        jwt.sign("secret");
//
//        ObjectMapper om = new ObjectMapper();
//        System.err.println("secret base64 = " + Base64.encodeBase64URLSafeString("secretsecret".getBytes()));
////        System.err.println("secret base64 = " + Base64.getUrlEncoder().withoutPadding().encodeToString("secret".getBytes()));
//        System.err.println(om.writeValueAsString(jwt.getHeader()));
//        System.err.println(om.writeValueAsString(jwt.getPayload()));
//        System.err.println(om.writeValueAsString(jwt.getSignature()));
//
////        String data = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9";
////        String data = parts[0] + "." + parts[1];
//        String part0 = om.writeValueAsString(jwt.getHeader());
//        String part1 = om.writeValueAsString(jwt.getPayload());
//        
////        String data = Base64.getUrlEncoder().withoutPadding().encodeToString((part0+"."+part1).getBytes());
//        String data = (Base64.encodeBase64URLSafeString(part0.getBytes()))+"."+(Base64.encodeBase64URLSafeString(part1.getBytes()));
//
//        byte[] base64 = calcHmacSha256(Base64.decodeBase64URLSafe("secret"), data.getBytes());
////        System.err.println(Base64.encodeBase64URLSafeString(base64));
//        System.err.println(Base64.encodeBase64URLSafeString(base64));
//        
//        
//    }
}
