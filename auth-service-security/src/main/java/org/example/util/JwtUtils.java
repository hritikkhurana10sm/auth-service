package org.example.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.example.model.UserModel;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {
    private String secretKey = "xxx-yyy-zzz";

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String generateToken(UserModel userModel) {
        Claims claims = Jwts.claims().setSubject(userModel.getId());
        Date now = new Date();
        Date validity = new Date(now.getTime() + 60 * 60 * 1000);
        String jwtToken = Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(validity).signWith(SignatureAlgorithm.HS256, secretKey).compact();
        return jwtToken;
    }

    public String getToken(HttpServletRequest httpRequest) {
        String requestHeader = httpRequest.getHeader("Authorization");
        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
            return requestHeader.split(" ")[1];
        }
        return null;
    }

    public UserModel getUserFromToken(String jwtToken) throws IOException {
        String requestPayload = jwtToken.split("\\.")[1];
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedRequestPayload = decoder.decode(requestPayload);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        Map<String, Object> userPayload = objectMapper.readValue(decodedRequestPayload, new TypeReference<>() {
        });
        UserModel userModel = new UserModel();
        userModel.setId(userPayload.get("sub").toString());
        return userModel;
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println(e);
        }
        return null;
    }
}
