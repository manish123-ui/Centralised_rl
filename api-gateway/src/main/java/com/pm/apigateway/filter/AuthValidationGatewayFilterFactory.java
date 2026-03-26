package com.pm.apigateway.filter;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

public class AuthValidationGatewayFilterFactory extends
        AbstractGatewayFilterFactory<Object> {
    private final WebClient webClient;
    public AuthValidationGatewayFilterFactory(WebClient.Builder webClientBuilder,
                                             @Value("${auth.service.url}") String authServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(authServiceUrl).build();
    }
    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String token =
                    exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if(token == null || !token.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            return webClient.get()
                    .uri("/validate")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .flatMap(response -> {

                        String companyId = response.get("userId").toString();
                        // 3️⃣ Add headers to request
                        ServerHttpRequest modifiedRequest =
                                exchange.getRequest().mutate()
                                        .header("X-Company-Id", companyId)
                                        .build();

                        // 4️⃣ Continue filter chain
                        return chain.filter(
                                exchange.mutate()
                                        .request(modifiedRequest)
                                        .build()
                        );

                    });
    };
}
};
