package poker.texasholdem.api.user.service;

import java.util.List;

import poker.texasholdem.api.security.User;

public interface IUserService {
	User getCurrentUser();

	List<User> getUsers();

	User getUser(String email);

	void saveUser(User user);
}
