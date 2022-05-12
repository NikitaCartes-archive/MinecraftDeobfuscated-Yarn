/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.filter;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TextStream {
    public static final TextStream UNFILTERED = new TextStream(){

        @Override
        public void onConnect() {
        }

        @Override
        public void onDisconnect() {
        }

        @Override
        public CompletableFuture<Message> filterText(String text) {
            return CompletableFuture.completedFuture(Message.permitted(text));
        }

        @Override
        public CompletableFuture<List<Message>> filterTexts(List<String> texts) {
            return CompletableFuture.completedFuture((List)texts.stream().map(Message::permitted).collect(ImmutableList.toImmutableList()));
        }
    };

    public void onConnect();

    public void onDisconnect();

    public CompletableFuture<Message> filterText(String var1);

    public CompletableFuture<List<Message>> filterTexts(List<String> var1);

    public static class Message {
        public static final Message EMPTY = new Message("", "");
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

        public static Message permitted(String text) {
            return new Message(text, text);
        }

        public static Message censored(String raw) {
            return new Message(raw, "");
        }

        public boolean hasFilteredText() {
            return !this.raw.equals(this.filtered);
        }
    }
}

