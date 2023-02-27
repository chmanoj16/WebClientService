package com.demo.service.webClientService.controller;

import com.demo.service.webClientService.model.Guest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Controller
public class ClientController {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    WebClient webClient;

    @GetMapping("/guests")
    public String getGuests(Model model,
                            @RegisteredOAuth2AuthorizedClient("web-client-oidc") OAuth2AuthorizedClient authorizedClient) {

        String jwtAccessToken = authorizedClient.getAccessToken().getTokenValue();
        System.out.println("jwtAccessToken = " + jwtAccessToken);

        String url = "http://localhost:8082/guest";
        /*HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + jwtAccessToken);
        HttpEntity<List<Guest>> httpEntity = new HttpEntity<>(httpHeaders);*/
        ResponseEntity<List<Guest>> response = restTemplate
                .exchange(url, HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<List<Guest>>() {
                });


        List<Guest> guests = response.getBody();
        /*List<Guest> guests = webClient
                .get()
                .uri(url)
                .attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Guest>>() {})
                .block();*/
        model.addAttribute("guests", guests);
        return "guests";
    }
}
