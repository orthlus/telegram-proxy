package main;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.tables.TelegramBotsInfo;
import main.tables.TelegramBotsSecrets;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

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

	public Set<Bot> getBots() {
		return db.select(tbi.NICKNAME, tbs.SECRET, tbi.HANDLER_URL_PATH, tbi.SERVICE_URL, tbi.TOKEN)
				.from(tbi)
				.leftJoin(tbs).on(tbi.NICKNAME.eq(tbs.NICKNAME))
				.fetchSet(mapping(Bot::new));
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
}
