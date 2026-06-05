package com.pm.apigateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class AuthValidationGatewayFilterFactory extends
        AbstractGatewayFilterFactory<Object> {

    private final WebClient webClient;

    public AuthValidationGatewayFilterFactory(WebClient.Builder webClientBuilder,
                                              @Value("${auth.service.url}") String authServiceUrl) {
        super(Object.class); // Explicitly pass the config class type to the parent constructor
        System.out.println("Auth service target URL: " + authServiceUrl);
        this.webClient = webClientBuilder.baseUrl(authServiceUrl).build();
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            // 1. Validate if token is present and starts with Bearer
            if (token == null || !token.startsWith("Bearer ")) {
                System.out.println("Missing or invalid authorization header format.");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            System.out.println("Forwarding token verification request to auth-service...");

            return webClient.get()
                    .uri("/validate")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    // 2. Safely handle HTTP Status Errors from the validation microservice
                    .onStatus(HttpStatus.UNAUTHORIZED::equals, clientResponse -> {
                        System.out.println("Auth-service rejected token: 401 Unauthorized");
                        return Mono.error(new RuntimeException("Unauthorized token"));
                    })
                    .bodyToMono(Map.class)
                    .flatMap(response -> {
                        // 3. Prevent NullPointerException if response key is missing
                        if (response == null || !response.containsKey("userId")) {
                            System.out.println("Auth response missing expected key 'userId'");
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        }

                        String userId = String.valueOf(response.get("userId"));
                        System.out.println("Token validated successfully for User/Company ID: " + userId);

                        // 4. Mutate and append user tracking header to down-stream requests
                        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                                .header("X-Company-Id", userId)
                                .build();

                        return chain.filter(
                                exchange.mutate().request(modifiedRequest).build()
                        );
                    })
                    // 5. Catch-all for routing/network connection timeouts
                    .onErrorResume(error -> {
                        System.err.println("Authentication Filter Error: " + error.getMessage());
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    });
        };
    }
}