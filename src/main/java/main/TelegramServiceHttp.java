package main;

import feign.Headers;
import feign.RequestLine;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.net.URI;

public interface TelegramServiceHttp {
	@RequestLine("POST")
	@Headers("content-type: application/json")
	BotApiMethod<?> send(URI uri, Update update);
}
