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
		public CompletableFuture<TextStream.Message> filterText(String text) {
			return CompletableFuture.completedFuture(TextStream.Message.permitted(text));
		}

		@Override
		public CompletableFuture<List<TextStream.Message>> filterTexts(List<String> texts) {
			return CompletableFuture.completedFuture((List)texts.stream().map(TextStream.Message::permitted).collect(ImmutableList.toImmutableList()));
		}
	};

	void onConnect();

	void onDisconnect();

	CompletableFuture<TextStream.Message> filterText(String text);

	CompletableFuture<List<TextStream.Message>> filterTexts(List<String> texts);

	public static class Message {
		public static final TextStream.Message EMPTY = new TextStream.Message("", "");
		private final String raw;
		private final String filtered;

		public Message(String raw, String filtered) {
			this.raw = raw;
			this.filtered = filtered;
		}

		public String getRaw() {
			return this.raw;
		}

		public String getFiltered() {
			return this.filtered;
		}

		public static TextStream.Message permitted(String text) {
			return new TextStream.Message(text, text);
		}

		public static TextStream.Message censored(String raw) {
			return new TextStream.Message(raw, "");
		}
	}
}
