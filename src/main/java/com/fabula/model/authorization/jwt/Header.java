/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.model.authorization.jwt;

import java.util.HashMap;



/**
 *
 * @author caio
 */
public class Header  extends HashMap<String, Object> implements JwtComponent{
//https://datatracker.ietf.org/doc/html/rfc7519#section-4.1
    static String TYP = "typ";
    static String ALG = "alg";
    static String ISS = "iss";//issuer
    static String SUB = "sub";//subject
    static String AUD = "aud";//audience
    static String CTY = "cty";//contentType
    
    public Header(){
        this.put(TYP, "jwt");
        this.put(ALG, "HS256");
        
    }
    
    public Header(String base64) throws InvalidJwtException{
        this.decodeBase64Url(base64);
    }
    
    public Header(String type, String alg) {
        this.put(TYP, type);
        this.put(ALG, alg);
    }

    
}
