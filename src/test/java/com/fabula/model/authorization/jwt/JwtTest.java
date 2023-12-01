package com.fabula.model.authorization.jwt;

import static com.fabula.model.authorization.jwt.Signature.calcHmacSha256;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.Assert;

/**
 *
 * @author caio
 */
public class JwtTest {

    public JwtTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testSignature() {

        try {
            String bearer = "eyJ0eXAiOiJqd3QiLCJhbGciOiJIUzI1NiJ9."
                    + "eyJzdWIiOiJyb290QGRvbWFpbi5jb20iLCJkb21haW5zIjp7ImRkYWYyYTczLWQ3MjAtNDNlNC05NmRmLTUzMzk3ZmU2ZGM3MyI6IkZhYnVsYSBQcm9qZWN0IiwiNzVlZDFiNDItZGU0NC00NzgzLThjYTQtZDE0MWNlNTJhMzY3IjoiRmFidWxhIiwiOGEwNzFlNzEtNzk1Zi00ZWFjLWFjNGEtY2MzMzRmYmU1ZDQ3IjoiRmFidWxhIEJsb2cifSwiZXhwIjoxNjQ5MDEzMDIwMDk2LCJpYXQiOjE2NDg5OTUwMjAwOTZ9."
                    + "zU1ip-_TXnlkKN0MNxPug-9pWtOzhw5oDfcoxGmsZCg";
            //the end of the bearer, the signature, is wrong, it will be regenerated.
            Jwt jwt = new Jwt(bearer);
            String secret = "CCY9C-YdWSZ0hbL_S3STGHcpEDsmrOrzFhUmMGkiApSuFE4J";
            jwt.sign(secret);

            ObjectMapper om = new ObjectMapper();
            String part0 = om.writeValueAsString(jwt.getHeader());
            String part1 = om.writeValueAsString(jwt.getPayload());

            String data = (Base64.encodeBase64URLSafeString(part0.getBytes())) + "." + (Base64.encodeBase64URLSafeString(part1.getBytes()));
            byte[] base64 = calcHmacSha256("CCY9C-YdWSZ0hbL_S3STGHcpEDsmrOrzFhUmMGkiApSuFE4J".getBytes(), data.getBytes());
            String signature = Base64.encodeBase64URLSafeString(base64);
            
            Assert.assertEquals(signature, jwt.getSignature().getEncoded());
            Assert.assertTrue(jwt.verifySignature(secret));
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }

}
