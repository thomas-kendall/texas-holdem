package poker.texasholdem.api.user.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;

import poker.texasholdem.api.security.Role;
import poker.texasholdem.api.security.User;

@Service
public class UserService implements IUserService {

	private final Map<String, User> usersByEmail;

	public UserService() {
		usersByEmail = new HashMap<>();

		User user = new User();
		user.setName("Thomas");
		user.setEmail("thomas.j.kendall@gmail.com");
		user.addRole(Role.ADMIN);
		user.addRole(Role.PLAYER);
		saveUser(user);

		user = new User();
		user.setName("Zak");
		user.setEmail("stonewall.kendall@gmail.com");
		user.addRole(Role.PLAYER);
		saveUser(user);

//		user = new User();
//		user.setName("Zy");
//		user.setEmail("zylerkendall@gmail.com");
//		user.addRole(Role.PLAYER);
//		saveUser(user);
	}

	@Override
	public User getCurrentUser() {
		User currentUser = null;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) authentication.getPrincipal();
			String email = principal.getAttribute("email");
			currentUser = getUser(email);
			if (currentUser == null) {
				// The user has not been added to the system, so return a user with no roles
				currentUser = new User();
				currentUser.setEmail(email);
			}
		}

		return currentUser;
	}

	@Override
	public List<User> getUsers() {
		return usersByEmail.values().stream().sorted(Comparator.comparing(User::getName)).toList();
	}

	@Override
	public User getUser(String email) {
		return usersByEmail.containsKey(email) ? usersByEmail.get(email) : null;
	}

	@Override
	public void saveUser(User user) {
		usersByEmail.put(user.getEmail(), user);
	}

}
