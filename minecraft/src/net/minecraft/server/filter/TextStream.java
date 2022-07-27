package net.minecraft.server.filter;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TextStream {
	TextStream UNFILTERED = new TextStream() {
		@Override
		public void onConnect() {
		}

		@Override
		public void onDisconnect() {
		}

		@Override
		public CompletableFuture<FilteredMessage> filterText(String text) {
			return CompletableFuture.completedFuture(FilteredMessage.permitted(text));
		}

		@Override
		public CompletableFuture<List<FilteredMessage>> filterTexts(List<String> texts) {
			return CompletableFuture.completedFuture((List)texts.stream().map(FilteredMessage::permitted).collect(ImmutableList.toImmutableList()));
		}
	};

	void onConnect();

	void onDisconnect();

	CompletableFuture<FilteredMessage> filterText(String text);

	CompletableFuture<List<FilteredMessage>> filterTexts(List<String> texts);
}
