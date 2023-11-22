package main;

import feign.Feign;
import feign.Response;
import feign.Target;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.lang.reflect.Type;

import static java.net.URI.create;

@Component
public class BotsService {
	@Value("${telegram.admin.id}")
	private long adminId;
	private final TelegramServiceHttp telegramServiceHttp = Feign.builder()
			.encoder(new JacksonEncoder())
			.decoder(this::decodeTelegramTypes)
			.target(Target.EmptyTarget.create(TelegramServiceHttp.class));
	private final Type[] telegramResponseTypes = {SendMessage.class, DeleteMessage.class};

	private Object decodeTelegramTypes(Response response, Type type) {
		for (Type telegramType : telegramResponseTypes) {
			try {
				return new JacksonDecoder().decode(response, telegramType);
			} catch (IOException ignored) {
			}
		}

		throw new RuntimeException("Unknown telegram type!");
	}

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
