package com.example.shopappbackend.jwt;

import com.example.shopappbackend.exception.ErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.internal.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    @Value("${api.prefix}")
    private String apiPrefix;

    @PostConstruct
    public void init() {
        this.apiPrefix = apiPrefix.trim();
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request
            , @NonNull HttpServletResponse response
            , @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            if (isBypassToken(request)) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = getTokenFromRequest(request);
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsername(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setContentType("application/json");
                response.setContentType("application/json; charset=UTF-8");  // Đảm bảo sử dụng UTF-8
                response.setCharacterEncoding("UTF-8");
                ErrorException<String> errorResponse = new ErrorException<>("Quyền truy cập bị hạn chế vui lòng kiểm tra lại", request.getRequestURI());
                ObjectMapper mapper = new ObjectMapper();
                response.getWriter().write(mapper.writeValueAsString(errorResponse));
                return;
            }

        } catch (Exception e) {
            logger.error("Failed to authenticate user", e);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            ErrorException<String> errorResponse = new ErrorException<>("Quyền truy cập bị hạn chế vui lòng kiểm tra lại", request.getRequestURI());
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(errorResponse));
            return;
        }

        filterChain.doFilter(request, response);
    }


    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }


    private boolean isBypassToken(@NonNull HttpServletRequest request) {
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of(String.format("/%s/products", apiPrefix), "GET"),
                Pair.of(String.format("/%s/products/search", apiPrefix), "POST"),
                Pair.of(String.format("/%s/categories", apiPrefix), "GET"),
                Pair.of(String.format("/%s/product-images", apiPrefix), "GET"),
                Pair.of(String.format("/%s/users/register", apiPrefix), "POST"),
                Pair.of(String.format("/%s/users/login", apiPrefix), "POST")
        );
        for (Pair<String, String> bypassToken : bypassTokens) {
            if (request.getServletPath().startsWith(bypassToken.getLeft())
                    && request.getMethod().equalsIgnoreCase(bypassToken.getRight())) {
                return true;
            }
        }
        return false;
    }
}
