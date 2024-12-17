package com.zufar.icedlatte.review.filter;

import static com.zufar.icedlatte.review.endpoint.ProductReviewEndpoint.PRODUCT_REVIEW_URL;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class QueryParamFilter extends OncePerRequestFilter {

    private static final String REVIEWS_URI_SUFFIX = "/reviews";
    private static final String INVALID_PAGE_SIZE_ERROR = "{\"timestamp\": \"%s\", \"status\": 400, \"error\": \"Invalid page size value\", \"path\": \"%s\"}";
    private static final String REGEX_ONLY_DIGITS = "^[0-9]+$";

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
       @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        if (requestURI.startsWith(PRODUCT_REVIEW_URL) && requestURI.contains(REVIEWS_URI_SUFFIX)) {
            if (isRequestWithInvalidPageSize(request)) { // Allow only numeric values
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write(String.format(INVALID_PAGE_SIZE_ERROR, LocalDateTime.now(), request.getRequestURI()));
                return;
            }
        }

        // Continue the filter chain if validation passes
        filterChain.doFilter(request, response);
    }

    private static boolean isRequestWithInvalidPageSize(HttpServletRequest request) {
        String rawQuery = request.getQueryString();
        String rawPageSize = request.getParameter("size");

        return rawPageSize != null && !rawPageSize.matches(REGEX_ONLY_DIGITS) || rawQuery.contains(
            "size=%");
    }
}
