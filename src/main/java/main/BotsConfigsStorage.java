package main;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

@Component
@RequiredArgsConstructor
public class BotsConfigsStorage {
	@Getter
	private Set<Bot> bots;
	private Map<String, String> secretsByNickname;
	private final BotsRepository repo;
	private final WebhookRegisterService webhookRegisterService;

	@PostConstruct
	private void init() {
		loadBots();
		webhookRegisterService.registerWebhooksForBotsWithoutSecret(bots);
		loadBots();
	}

	public void loadBots() {
		bots = repo.getBots();
		secretsByNickname = bots.stream()
				.filter(Bot::hasSecret)
				.collect(toMap(Bot::nickname, Bot::secret, (a, b) -> b));
	}

	public String secretByNickname(String nickname) {
		return secretsByNickname.get(nickname);
	}
}
