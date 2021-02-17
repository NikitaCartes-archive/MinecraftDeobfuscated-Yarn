package net.minecraft.server.filter;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TextStream {
	TextStream field_28862 = new TextStream() {
		@Override
		public void onConnect() {
		}

		@Override
		public void onDisconnect() {
		}

		@Override
		public CompletableFuture<TextStream.class_5837> filterText(String text) {
			return CompletableFuture.completedFuture(TextStream.class_5837.method_33802(text));
		}

		@Override
		public CompletableFuture<List<TextStream.class_5837>> filterTexts(List<String> texts) {
			return CompletableFuture.completedFuture(texts.stream().map(TextStream.class_5837::method_33802).collect(ImmutableList.toImmutableList()));
		}
	};

	void onConnect();

	void onDisconnect();

	CompletableFuture<TextStream.class_5837> filterText(String text);

	CompletableFuture<List<TextStream.class_5837>> filterTexts(List<String> texts);

	public static class class_5837 {
		public static final TextStream.class_5837 field_28863 = new TextStream.class_5837("", "");
		private final String field_28864;
		private final String field_28865;

		public class_5837(String string, String string2) {
			this.field_28864 = string;
			this.field_28865 = string2;
		}

		public String method_33801() {
			return this.field_28864;
		}

		public String method_33803() {
			return this.field_28865;
		}

		public static TextStream.class_5837 method_33802(String string) {
			return new TextStream.class_5837(string, string);
		}

		public static TextStream.class_5837 method_33804(String string) {
			return new TextStream.class_5837(string, "");
		}
	}
}
