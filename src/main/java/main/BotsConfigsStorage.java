package main;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.mapstruct.factory.Mappers.getMapper;

@Component
@RequiredArgsConstructor
public class BotsConfigsStorage {
	@Getter
	private Set<Bot> bots;
	private Map<String, String> secretsByNickname;
	private final BotsRepository repo;

	@PostConstruct
	private void init() {
		Set<BotDto> repoBots = repo.getBots();

		bots = getMapper(BotsMapper.class).map(repoBots);

		secretsByNickname = new HashMap<>();
		for (Bot bot : bots) {
			secretsByNickname.put(bot.nickname(), bot.secret());
		}
	}

	public String secretByNickname(String nickname) {
		return secretsByNickname.get(nickname);
	}
}
