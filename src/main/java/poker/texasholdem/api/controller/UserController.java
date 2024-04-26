package poker.texasholdem.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public List<User> getUsers(@RequestHeader Map<String, String> headers) {
		User currentUser = userService.getCurrentUser();
		return userService.getUsers();
	}
}
