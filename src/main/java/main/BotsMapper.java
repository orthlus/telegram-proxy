package main;

import org.mapstruct.Mapper;

import java.net.URI;
import java.util.Set;

@Mapper
public interface BotsMapper {
	Bot map(BotDto botDto);

	Set<Bot> map(Set<BotDto> botsDto);

	default URI uri(String urlStr) {
		return URI.create(urlStr);
	}
}
