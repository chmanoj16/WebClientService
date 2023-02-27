package com.demo.service.webClientService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class WebClientServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebClientServiceApplication.class, args);
	}

	@Bean
	RestTemplate restTemplate() {
		RestTemplate rest = new RestTemplate();
		rest.getInterceptors().add((request, body, execution) -> {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null) {
				return execution.execute(request, body);
			}

			if (!(authentication.getCredentials() instanceof AbstractOAuth2Token)) {
				return execution.execute(request, body);
			}

			AbstractOAuth2Token token = (AbstractOAuth2Token) authentication.getCredentials();
			System.out.println("NEW TOKEN = " + token.getTokenValue());
			request.getHeaders().setBearerAuth(token.getTokenValue());
			return execution.execute(request, body);
		});
		return rest;
	}

	@Bean
	public WebClient webClient(ClientRegistrationRepository clientRegistrationRepository,
							   OAuth2AuthorizedClientRepository authorizedClientRepository) {
		ServletOAuth2AuthorizedClientExchangeFilterFunction servletOAuth2AuthorizedClientExchangeFilterFunction
				= new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository, authorizedClientRepository);
		return WebClient.builder()
				.apply(servletOAuth2AuthorizedClientExchangeFilterFunction.oauth2Configuration())
				.build();
	}

}
