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
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
//import org.apache.tomcat.util.codec.binary.Base64;

/**
 *
 * @author caio
 */
public interface JwtComponent extends Map<String, Object> {

    static ObjectMapper mapper = new ObjectMapper();

    public default String encode() throws IOException {

        try {
            String s = mapper.writeValueAsString(this);
            return encodeBase64Url(s);
        } catch (JsonProcessingException ex) {
            throw new IOException(ex);
        }

    }

    static String encodeBase64Url(String s) throws IOException {
        byte[] bytes = s.getBytes();//StandardCharsets.UTF_8);
//        return Base64.encodeBase64URLSafeString(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

    }

    public default void decodeBase64Url(String base64) throws InvalidJwtException {
        try {
//            byte[] asBytes = Base64.decodeBase64URLSafe(base64);
            byte[] asBytes = Base64.getUrlDecoder().decode(base64);
            this.entrySet().clear();
            Map<String, Object> temp = mapper.readValue(asBytes, HashMap.class);
            this.clear();
            this.putAll(temp);
        } catch (Exception ex) {
            throw new InvalidJwtException();
        }

    }

}
