package main;

import feign.Headers;
import feign.RequestLine;

import java.net.URI;
import java.util.Map;

public interface TelegramApiHttp {
	@RequestLine("POST")
	@Headers("Content-Type: application/x-www-form-urlencoded")
	void register(URI uri, Map<String, ?> params);
}
