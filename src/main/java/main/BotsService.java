package main;

import feign.Feign;
import feign.Target;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import static java.net.URI.create;

@Component
public class BotsService {
	@Value("${telegram.admin.id}")
	private long adminId;
	private final TelegramServiceHttp telegramServiceHttp = Feign.builder()
			.encoder(new JacksonEncoder())
			.decoder(new JacksonDecoder())
			.target(Target.EmptyTarget.create(TelegramServiceHttp.class));

	public BotApiMethod<?> request(Bot bot, Update update) {
		if (isAdmin(update)) {
			String url = bot.serviceUrl() + bot.handlerUrlPath();

			return telegramServiceHttp.send(create(url), update);
		}

		return null;
	}

	private boolean isAdmin(Update update) {
		if (update.hasMessage())
			return adminId == update.getMessage().getChat().getId();
		else if (update.hasCallbackQuery())
			return adminId == update.getCallbackQuery().getMessage().getChat().getId();
		else
			return false;
	}
}
