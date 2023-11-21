package main;

import feign.RequestLine;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.net.URI;

public interface TelegramServiceHttp {
	@RequestLine("POST")
	BotApiMethod<?> send(URI uri, Update update);
}
