package main;

import feign.Feign;
import feign.form.FormEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.UUID.randomUUID;
import static org.telegram.telegrambots.meta.ApiConstants.BASE_URL;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookRegisterService {
	@Value("${telegram.handler.external.domain}")
	private String telegramHandlerExternalDomain;
	private final TelegramApiHttp client = Feign.builder()
			.encoder(new FormEncoder())
			.target(TelegramApiHttp.class, BASE_URL);

	public Set<Bot> registerWebhooksForBotsWithoutSecret(Set<Bot> bots) {
		Set<Bot> botsWithNewSecrets = new HashSet<>();

		for (Bot bot : bots) {
			if (!bot.hasSecret()) {
				Bot botWithSecret = bot.withSecret(randomUUID());
				registerWebhook(botWithSecret);
				botsWithNewSecrets.add(botWithSecret);
			}
		}

		return botsWithNewSecrets;
	}

	private void registerWebhook(Bot bot) {
		client.register(setWebhookUri(bot), params(bot));
		log.info("bot {} webhook registered", bot.nickname());
	}


	private URI setWebhookUri(Bot bot) {
		return URI.create(BASE_URL + bot.token() + "/setWebhook");
	}

	private Map<String, ?> params(Bot bot) {
		return params(telegramHandlerExternalDomain + bot.handlerUrlPath(), bot.secret(), true);
	}

	private Map<String, ?> params(String url, String secretToken, boolean dropPendingUpdates) {
		return Map.of(
				"url", url,
				"secret_token", secretToken,
				"drop_pending_updates", dropPendingUpdates
		);
	}
}
