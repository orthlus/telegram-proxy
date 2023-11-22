package main;

import java.util.UUID;

public record Bot(String nickname, String secret, String handlerUrlPath, String serviceUrl, String token) {
	public Bot withSecret(UUID secret) {
		return withSecret(secret.toString());
	}

	public Bot withSecret(String secret) {
		return new Bot(nickname, secret, handlerUrlPath, serviceUrl, token);
	}

	public boolean hasSecret() {
		return secret != null;
	}
}
