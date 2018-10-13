package org.obapi.squadx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class APIController {

	private static final String baseURL = "https://api.hsbc.qa.xlabs.one/invoauth2";
	
	@Autowired
	RestTemplate restTemplate;

	@RequestMapping("/")
	public String index() {
		return "Welcome to Squad-x Open Banking APIs use /api to navigate to APIs";
	}

	@RequestMapping("/api")
	public String api() {
		String result = getClientAssertion();
		return result;
	}

	private String getClientAssertion() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");

		MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
		//TODO: Use iss from Portal hint - Sharad App
		bodyMap.add("iss", "==USE FROM PORTAL==");
		bodyMap.add("sub", "squadx-open-banking");
		bodyMap.add("jti", "squadx-open-banking");
		bodyMap.add("aud", "https://api.hsbc.qa.xlabs.one/as/token.oauth2");

		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(bodyMap, headers);
		String clientAssertion = restTemplate.postForObject(baseURL + "/client-assertion", requestEntity, String.class);
		String token = getTokenClientCredentials(clientAssertion.replace("\"", ""));
		
		return token;
	}

	private String getTokenClientCredentials(String clientAssertion) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/x-www-form-urlencoded");

		MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
		bodyMap.add("grant_type", "client_credentials");
		bodyMap.add("scope", "accounts");
		bodyMap.add("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
		bodyMap.add("client_assertion", clientAssertion);

		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(bodyMap, headers);
		String result = restTemplate.postForObject(baseURL + "/as/token-client-credentials", requestEntity, String.class);
		return result;
	}
}
