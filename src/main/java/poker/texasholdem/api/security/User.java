package poker.texasholdem.api.security;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class User {
	private String email;
	private List<Role> roles = new ArrayList<>();

	public void addRole(Role role) {
		if (!this.roles.contains(role)) {
			this.roles.add(role);
		}
	}
}
