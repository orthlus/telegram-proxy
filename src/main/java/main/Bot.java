package main;

import java.net.URI;

public record Bot(String nickname, String secret, URI handlerUrl) {
}
