/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.model.authorization.jwt;

import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author caio
 */
public class Payload extends HashMap<String, Object> implements JwtComponent {

    static String ISS = "iss";//issuer
    static String SUB = "sub";//subject
    static String AUD = "aud";//audience
    static String EXP = "exp";//expirationDate
    static String NBF = "nbf";//notBefore
    static String IAT = "iat";//issuedAt
    static String JTI = "jti";//JWT id

    public Payload() {

    }

    public Payload(String base64) throws InvalidJwtException{
        this.decodeBase64Url(base64);
    }
    
    public String getIssuer() {
        return this.get(ISS, String.class);
    }

    public Payload setIssuer(String issuer) {
        super.put(ISS, issuer);
        return this;
    }

    public String getSubject() {
        return this.get(SUB, String.class);
    }

    public Payload setSubject(String subject) {
        super.put(SUB, subject);
        return this;
    }

    public String getAudience() {
        return this.get(AUD, String.class);
    }

    public Payload setAudience(String audience) {
        super.put(AUD, audience);
        return this;
    }

    public Long getExpiration() {
        return this.get(EXP, Long.class);
    }

    public Payload setExpiration(Long date) {
        super.put(EXP, date);
        return this;
    }

    public Long getNotBefore() {
        return this.get(NBF, Long.class);
    }

    public Payload setNotBefore(Long date) {
        super.put(NBF, date);
        return this;
    }

    public Long getIssuedAt() {
        return this.get(IAT, Long.class);
    }

    public Payload setIssuedAt(Long date) {
        super.put(IAT, date);
        return this;
    }

    public String getId() {
        return this.get(JTI, String.class);
    }

    public Payload setId(String id) {
        super.put(JTI, id);
        return this;
    }

    public <T extends Object> T get(String claim, Class<T> type) {
        return (T) super.get(claim);
    }
    
    public Payload set(String claim, Object value){
        super.put(claim, value);
        return this;
    }
}
