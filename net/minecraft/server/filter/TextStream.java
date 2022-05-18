/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.filter;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public interface TextStream {
    public static final TextStream UNFILTERED = new TextStream(){

        @Override
        public void onConnect() {
        }

        @Override
        public void onDisconnect() {
        }

        @Override
        public CompletableFuture<FilteredMessage<String>> filterText(String text) {
            return CompletableFuture.completedFuture(FilteredMessage.permitted(text));
        }

        @Override
        public CompletableFuture<List<FilteredMessage<String>>> filterTexts(List<String> texts) {
            return CompletableFuture.completedFuture((List)texts.stream().map(FilteredMessage::permitted).collect(ImmutableList.toImmutableList()));
        }
    };

    public void onConnect();

    public void onDisconnect();

    public CompletableFuture<FilteredMessage<String>> filterText(String var1);

    public CompletableFuture<List<FilteredMessage<String>>> filterTexts(List<String> var1);

    default public CompletableFuture<FilteredMessage<Text>> filterText(Text text) {
        return this.filterText(text.getString()).thenApply(filteredMessage -> {
            Text text2 = Util.map((String)filteredMessage.filtered(), Text::literal);
            return new FilteredMessage<Text>(text, text2);
        });
    }
}

