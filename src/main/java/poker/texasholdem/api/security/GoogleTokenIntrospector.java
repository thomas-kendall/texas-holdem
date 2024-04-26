package poker.texasholdem.api.security;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import poker.texasholdem.api.user.service.IUserService;

public class GoogleTokenIntrospector implements OpaqueTokenIntrospector {

	private final RestTemplate restTemplate = new RestTemplate();
	private final String introspectionUri;
	private final IUserService userService;

	public GoogleTokenIntrospector(final IUserService userService) {
		this.introspectionUri = "https://oauth2.googleapis.com/tokeninfo";
		this.userService = userService;
	}

	@Override
	public OAuth2AuthenticatedPrincipal introspect(String token) {
		RequestEntity<?> requestEntity = buildRequest(token);
		try {
			ResponseEntity<Map<String, Object>> responseEntity = this.restTemplate.exchange(requestEntity,
					new ParameterizedTypeReference<>() {
					});

			String email = (String) responseEntity.getBody().get("email");
			User user = userService.getUser(email);
			String name = email;

			// Build the attributes map
			Map<String, Object> attributes = new HashMap<>();
			responseEntity.getBody().entrySet().stream().forEach(e -> {
				String key = e.getKey();
				Object value = e.getValue();
				if (key.equals(OAuth2TokenIntrospectionClaimNames.IAT)
						|| key.equals(OAuth2TokenIntrospectionClaimNames.EXP)) {
					attributes.put(key, Instant.ofEpochSecond(Long.parseLong((String) value)));
				} else {
					attributes.put(key, value);
				}
			});

			// Build the granted authorities
			Collection<GrantedAuthority> authorities = new ArrayList<>();
			if (user != null) {
				user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.name())));
			}

			return new DefaultOAuth2AuthenticatedPrincipal(name, attributes, authorities);
		} catch (Exception ex) {
			throw new BadOpaqueTokenException(ex.getMessage(), ex);
		}
	}

	private RequestEntity<?> buildRequest(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("access_token", token);

		return new RequestEntity<>(body, headers, HttpMethod.POST, URI.create(introspectionUri));
	}

}
