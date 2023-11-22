package main;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.tables.TelegramBotsInfo;
import main.tables.TelegramBotsSecrets;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

import static main.Tables.TELEGRAM_BOTS_INFO;
import static main.Tables.TELEGRAM_BOTS_SECRETS;
import static org.jooq.Records.mapping;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotsRepository {
	private final DSLContext db;
	private final TelegramBotsInfo tbi = TELEGRAM_BOTS_INFO;
	private final TelegramBotsSecrets tbs = TELEGRAM_BOTS_SECRETS;

	public Set<BotDto> getBots() {
		return db.select(tbi.NICKNAME, tbs.SECRET, tbi.URL)
				.from(tbi)
				.leftJoin(tbs).on(tbi.NICKNAME.eq(tbs.NICKNAME))
				.fetchSet(mapping(BotDto::new));
	}

	public Map<String, String> getSecretsMap() {
		return db.select(tbs.NICKNAME, tbs.SECRET)
				.from(tbs)
				.fetchMap(tbs.NICKNAME, tbs.SECRET);
	}

	public void saveSecret(String nickname, String secret) {
		db.insertInto(tbs)
				.columns(tbs.NICKNAME, tbs.SECRET)
				.values(nickname, secret)
				.onDuplicateKeyUpdate()
				.set(tbs.SECRET, secret)
				.where(tbs.NICKNAME.eq(nickname))
				.execute();
	}

	public void updateBotUrl(String nickname, String url) {
		db.update(tbi)
				.set(tbi.URL, url)
				.where(tbi.NICKNAME.eq(nickname))
				.execute();
	}

	public void addBot(String nickname, String url) {
		db.insertInto(tbi)
				.columns(tbi.NICKNAME, tbi.URL)
				.values(nickname, url)
				.onDuplicateKeyIgnore()
				.execute();
	}
}
