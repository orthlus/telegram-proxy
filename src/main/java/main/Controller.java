package main;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestController
@RequestMapping("/telegram")
@RequiredArgsConstructor
public class Controller {
	private final BotsConfigsStorage botsConfigsStorage;
	private final BotsService botsService;

	@PostMapping("{requestPath}")
	public ResponseEntity<BotApiMethod<?>> update(@RequestBody Update update,
												  HttpServletRequest request,
												  @PathVariable String requestPath) {
		for (Bot bot : botsConfigsStorage.getBots()) {
			String nickname = bot.nickname();

			if (nickname.equals(requestPath)) {
				validSecret(request, requestPath);

				BotApiMethod<?> r = botsService.request(bot, update);
				return ResponseEntity.ok(r);
			}
		}

		log.info("/telegram controller: request to {} - not found handlers, return 404", requestPath);
		return ResponseEntity.notFound().build();
	}

	private void validSecret(HttpServletRequest request, String nickname) {
		try {
			String secret = getSecret(request);
			String botStoredSecret = botsConfigsStorage.secretByNickname(nickname);

			if (botStoredSecret == null) {
				log.error("failed validate telegram secret - secret not stored for bot {}", nickname);
				throw new TelegramErrorException();
			}
			if (!secret.equals(botStoredSecret)) {
				log.error("telegram request to {} with invalid secret, skipped", nickname);
				throw new TelegramErrorException();
			}
		} catch (TelegramSecretNotFoundException e) {
			log.error("telegram request to {} without secret, skipped", nickname);
			throw new TelegramErrorException();
		}
	}

	private String getSecret(HttpServletRequest request) {
		String secret = request.getHeader("X-Telegram-Bot-Api-Secret-Token");

		if (secret != null)
			return secret;
		else
			throw new TelegramSecretNotFoundException();
	}

	@ResponseStatus(NOT_FOUND)
	public static class TelegramErrorException extends RuntimeException {}
	public static class TelegramSecretNotFoundException extends RuntimeException {}
}
