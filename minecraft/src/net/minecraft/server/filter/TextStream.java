package net.minecraft.server.filter;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface TextStream {
	void onConnect();

	void onDisconnect();

	CompletableFuture<Optional<String>> filterText(String text);

	CompletableFuture<Optional<List<String>>> filterTexts(List<String> texts);
}
