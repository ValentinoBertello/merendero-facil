package com.merendero.facil.auth;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

public class TokenJwtConfig {

    public static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            "qSgZe3wXA2pPg9urO/1VU/PkylJ0NutmFux39ZAt4aE=".getBytes());

    public static final String PREFIX_TOKEN = "Bearer ";

    public static final String HEADER_AUTHORIZATION = "Authorization";

    public static final String CONTENT_TYPE = "application/json";

    //public static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();
}
