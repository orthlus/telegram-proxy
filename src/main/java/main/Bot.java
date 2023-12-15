package main;

import lombok.With;

public record Bot(String nickname, @With String secret, String handlerUrlPath, String serviceUrl, String token) {
	public boolean hasNoSecret() {
		return secret == null;
	}

	public boolean hasSecret() {
		return secret != null;
	}
}
