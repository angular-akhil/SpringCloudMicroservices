package com.appsdeveloperblog.photoapp.api.albums.security;


import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.appsdeveloperblog.JwtAuthorities.JwtClaimsParser;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthorizationFilter extends BasicAuthenticationFilter {
	private Environment environment;
	
    public AuthorizationFilter(AuthenticationManager authManager, Environment environment) {
        super(authManager);
        this.environment = environment;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain) throws IOException, ServletException {
    	Enumeration<String> headers = req.getHeaderNames();
    	while (headers.hasMoreElements()) {
    	    String headerName = headers.nextElement();
    	    System.out.println("Header: " + headerName + " = " + req.getHeader(headerName));
    	}

    	
        String header = req.getHeader(environment.getProperty("authorization.token.header.name"));

        if (header == null || !header.startsWith(environment.getProperty("authorization.token.header.prefix"))) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }
    
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {

        String authorizationHeader = request.getHeader(environment.getProperty("authorization.token.header.name"));

        if (authorizationHeader == null) {
            return null;
        }

        String token = authorizationHeader.replace(environment.getProperty("authorization.token.header.prefix"), "").trim();
        String tokenSecret = environment.getProperty("token.secret");

     // 🟨 Paste this right after the above line:
        System.out.println("USERS-WS " +token);
        System.out.println("USERS-WS length" +token.length());

     System.out.println("USERS-WS token.secret = [" + tokenSecret + "]");
     System.out.println("USERS-WS token.secret length = " + tokenSecret.length());
     System.out.println("USERS-WS token.secret byte length = " + tokenSecret.getBytes().length);
        


        JwtClaimsParser jwtClaimsParser = new JwtClaimsParser(token, tokenSecret);
        String subject = jwtClaimsParser.getJwtSubject();
        Collection<? extends GrantedAuthority> userAuthotiries = jwtClaimsParser.getUserAuthorities();
        
        return new UsernamePasswordAuthenticationToken(subject, null, userAuthotiries);

    }
}
