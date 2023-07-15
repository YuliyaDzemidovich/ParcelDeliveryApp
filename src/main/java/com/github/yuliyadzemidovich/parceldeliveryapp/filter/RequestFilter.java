package com.github.yuliyadzemidovich.parceldeliveryapp.filter;

import com.github.yuliyadzemidovich.parceldeliveryapp.security.SecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.FLOW_ID;
import static com.github.yuliyadzemidovich.parceldeliveryapp.Constants.REQUEST_HEADER;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestFilter extends OncePerRequestFilter {

    /**
     * Inserts flowId as UUID string for each incoming HTTP request.
     * This method sets flowId in the response header and everywhere in logs (MDC context) -
     * that allows request flow to be fully visible in logs.
     *
     * @param request http request
     * @param response http response
     * @param filterChain filter chain
     * @throws IOException if an I/O error occurs during the processing of the request
     * @throws ServletException if the processing fails for any other reason
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String flowIdOpt = request.getHeader(REQUEST_HEADER);
        String flowId = SecurityUtils.getOrGenerateUuid(flowIdOpt);

        // flowId in logs to track the request
        MDC.put(FLOW_ID, flowId);

        // same flowId in the response to allow tracking in next microservice
        response.addHeader(REQUEST_HEADER, flowId);
        filterChain.doFilter(request, response);
    }
}
