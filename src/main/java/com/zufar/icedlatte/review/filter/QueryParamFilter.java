package com.zufar.icedlatte.review.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class QueryParamFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String rawQuery = request.getQueryString();

        if (requestURI.startsWith("/api/v1/products/") && requestURI.contains("/reviews")) {
            String rawPageSize = request.getParameter("size");

            if (rawPageSize != null && !rawPageSize.matches("^[0-9]+$") || rawQuery.contains("size=%")) { // Allow only numeric values
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write("{\"timestamp\": \"" + LocalDateTime.now() + "\", \"status\": 400, \"error\": \"Invalid page size value\", \"path\": \"" + request.getRequestURI() + "\"}");
                return;
            }
        }

        // Continue the filter chain if validation passes
        filterChain.doFilter(request, response);
    }
}
