/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.message;

import java.util.concurrent.CompletableFuture;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

/**
 * Message decorator decorates the chat messages and other messages server-side.
 * Currently, only one message decorator can exist at a time. The message decorator
 * that is currently used can be obtained by
 * {@link net.minecraft.server.MinecraftServer#getMessageDecorator}.
 * 
 * <p>Messages decorated using message decorator are still marked as verifiable
 * if there is no change in its text or used fonts. If they change, the message cannot
 * be verified. Before 1.19.2, chat previews allowed signing of such message; however
 * that feature was removed in 1.19.3.
 */
@FunctionalInterface
public interface MessageDecorator {
    /**
     * An empty message decorator that returns the original message.
     */
    public static final MessageDecorator NOOP = (sender, message) -> CompletableFuture.completedFuture(message);

    public CompletableFuture<Text> decorate(@Nullable ServerPlayerEntity var1, Text var2);
}

