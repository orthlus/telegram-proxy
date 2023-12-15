package main;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.tables.TelegramBotsAddInfo;
import main.tables.TelegramBotsInfo;
import main.tables.TelegramBotsSecrets;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.util.Set;

import static main.Tables.*;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.and;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotsRepository {
	private final DSLContext db;
	private final TelegramBotsInfo tbi = TELEGRAM_BOTS_INFO;
	private final TelegramBotsSecrets tbs = TELEGRAM_BOTS_SECRETS;
	private final TelegramBotsAddInfo tbai = TELEGRAM_BOTS_ADD_INFO;

	public Set<String> getBotsCanUpdateWebhook() {
		Condition eq1 = tbai.KEY.eq("updateWebhooks");
		Condition eq2 = tbai.VALUE.eq("true");
		return db.select(tbai.NICKNAME)
				.from(tbai)
				.where(and(eq1, eq2))
				.fetchSet(tbai.NICKNAME);
	}

	public Set<Bot> getBots() {
		return db.select(tbi.NICKNAME, tbs.SECRET, tbi.HANDLER_URL_PATH, tbi.SERVICE_URL, tbi.TOKEN)
				.from(tbi)
				.leftJoin(tbs).on(tbi.NICKNAME.eq(tbs.NICKNAME))
				.fetchSet(mapping(Bot::new));
	}

	public void saveSecret(Bot bot) {
		saveSecret(bot.nickname(), bot.secret());
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
