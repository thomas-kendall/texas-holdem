package poker.texasholdem.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import poker.texasholdem.api.security.GoogleTokenIntrospector;
import poker.texasholdem.api.user.service.IUserService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	private final IUserService userService;

	public SecurityConfiguration(final IUserService userService) {
		this.userService = userService;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
				.oauth2ResourceServer(oAuth2ResourceServerConfigurer -> oAuth2ResourceServerConfigurer
						.opaqueToken(opaqueTokenConfigurer -> opaqueTokenConfigurer
								.introspector(new GoogleTokenIntrospector(userService))));
		return http.build();
	}
}
