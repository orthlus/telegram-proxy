package main;

import feign.Feign;
import feign.Target;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import static main.PropsProvider.getAdminId;

@Component
public class BotsService {
	private final TelegramServiceHttp telegramServiceHttp = Feign.builder()
			.encoder(new JacksonEncoder())
			.decoder(new JacksonDecoder())
			.target(Target.EmptyTarget.create(TelegramServiceHttp.class));

	public BotApiMethod<?> request(Bot bot, Update update) {
		if (isAdmin(update))
			return telegramServiceHttp.send(bot.handlerUrl(), update);

		throw new Controller.TelegramErrorException();

	}

	private boolean isAdmin(Update update) {
		if (update.hasMessage())
			return getAdminId() == update.getMessage().getChat().getId();
		else if (update.hasCallbackQuery())
			return getAdminId() == update.getCallbackQuery().getMessage().getChat().getId();
		else
			return false;
	}
}
