/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.model.authorization.jwt;

import static com.fabula.model.authorization.jwt.Payload.NBF;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author caio
 */
public class Jwt {

    private Header header;
    private Payload payload;
    private Signature signature;

    public Jwt() {
        this.header = new Header();
        this.payload = new Payload();
    }

    public Jwt(String bearer) throws InvalidJwtException {
        String[] components = bearer.split("\\.");
        if (components.length == 3) {
            this.header = new Header(components[0]);
            this.payload = new Payload(components[1]);
            this.signature = new Signature(components[2], this);
        } else {
            throw new InvalidJwtException();
        }
    }

    public Jwt sign(String secretKey) throws SecurityException {
        try {
            this.signature = new Signature(this, secretKey);
        } catch (InvalidKeyException | IOException ex) {
            throw new SecurityException(ex);
        }
        return this;
    }

    public boolean verifySignature(String secretKey) {
        if (this.signature == null) {
            return false;
        } else {
            try {
                Jwt temp = new Jwt();
                temp.header = this.header;
                temp.payload = this.payload;
                temp.signature = new Signature(temp, secretKey);
                
                return temp.signature.equals(this.signature);
            } catch (InvalidKeyException | IOException ex) {
                return false;
            }
        }
    }

    public Header getHeader() {
        return header;
    }

    public Payload getPayload() {
        return payload;
    }

    public Signature getSignature() {
        return signature;
    }

    public String getIssuer() {
        return payload.getIssuer();
    }

    public Jwt setIssuer(String issuer) {
        payload.setIssuer(issuer);
        return this;
    }

    public String getSubject() {
        return payload.getSubject();
    }

    public Jwt setSubject(String subject) {
        payload.setSubject(subject);
        return this;
    }

    public String getAudience() {
        return payload.getAudience();
    }

    public Jwt setAudience(String audience) {
        payload.setAudience(audience);
        return this;
    }

    public Long getExpiration() {
        return payload.getExpiration();
    }

    public Jwt setExpiration(Long date) {
        payload.setExpiration(date);
        return this;
    }

    public Long getNotBefore() {
        return this.get(NBF, Long.class);
    }

    public Jwt setNotBefore(Long date) {
        payload.setNotBefore(date);
        return this;
    }

    public Long getIssuedAt() {
        return payload.getIssuedAt();
    }

    public Jwt setIssuedAt(Long date) {
        payload.setIssuedAt(date);
        return this;
    }

    public String getId() {
        return payload.getId();
    }

    public Jwt setId(String id) {
        payload.setId(id);
        return this;
    }

    public boolean has(String claim) {
        return this.payload.containsKey(claim);
    }

    public <T extends Object> T get(String claim, Class<T> type) {
        return (T) payload.get(claim);
    }

    @Override
    public String toString() {
        try {
            return this.header.encode() + "." + this.payload.encode() + "." + this.signature.encode();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Jwt setClaim(String claim, Object value) {
        payload.set(claim, value);
        return this;

    }

}
