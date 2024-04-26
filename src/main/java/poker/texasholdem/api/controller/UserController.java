package poker.texasholdem.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import poker.texasholdem.api.model.SignUpRequest;
import poker.texasholdem.api.security.Role;
import poker.texasholdem.api.security.User;
import poker.texasholdem.api.user.service.IUserService;

@RestController
@RequestMapping("/users")
public class UserController {

	private IUserService userService;

	public UserController(final IUserService userService) {
		this.userService = userService;
	}

	@GetMapping
	public List<User> getUsers() {
		return userService.getUsers();
	}

	@GetMapping("/me")
	public User getMe() {
		User currentUser = userService.getCurrentUser();
		return currentUser;
	}

	@PostMapping("/sign-up")
	public User signUp(@RequestBody SignUpRequest request) {
		User currentUser = userService.getCurrentUser();
		currentUser.setName(request.getName());
		currentUser.addRole(Role.PLAYER);
		userService.saveUser(currentUser);
		return currentUser;
	}
}
