/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.filter;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.filter.FilteredMessage;

public interface TextStream {
    public static final TextStream UNFILTERED = new TextStream(){

        @Override
        public void onConnect() {
        }

        @Override
        public void onDisconnect() {
        }

        @Override
        public CompletableFuture<FilteredMessage> filterText(String text) {
            return CompletableFuture.completedFuture(FilteredMessage.method_45060(text));
        }

        @Override
        public CompletableFuture<List<FilteredMessage>> filterTexts(List<String> texts) {
            return CompletableFuture.completedFuture((List)texts.stream().map(FilteredMessage::method_45060).collect(ImmutableList.toImmutableList()));
        }
    };

    public void onConnect();

    public void onDisconnect();

    public CompletableFuture<FilteredMessage> filterText(String var1);

    public CompletableFuture<List<FilteredMessage>> filterTexts(List<String> var1);
}

