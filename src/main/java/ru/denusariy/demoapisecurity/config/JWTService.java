package ru.denusariy.demoapisecurity.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {
    private static final String SECRET_KEY = "33743677397A24432646294A404D635166546A576E5A7234753778214125442A";
    //достает имя пользователя из всех полей токена
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    //создает токен
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername()) //здесь будет email
                .setIssuedAt(new Date(System.currentTimeMillis())) //когда создано
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) //живет 24 часа
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) //алгоритм подписи
                .compact();
    }
    //создает токен попроще
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    //валидирует токен
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    //проверяет, что срок жизни токена не истек
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    //достает срок жизни токена
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    //достает поля из токена
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    //добавляет подпись к токену
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes); //алгоритм шифрования
    }
}
