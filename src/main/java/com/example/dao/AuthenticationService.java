package com.example.dao;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.entity.Cliente;

public class AuthenticationService {
    public String generateToken(Cliente cliente) {
        Algorithm algorithm = Algorithm.HMAC256("66Fj{9oB})"); // Use uma chave secreta segura
        return JWT.create()
                .withClaim("userId", cliente.getId())
                .withIssuer("your-issuer")
                .sign(algorithm);
    }
}
