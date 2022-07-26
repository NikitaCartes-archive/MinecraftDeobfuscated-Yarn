/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.message;

import java.util.Map;
import net.minecraft.network.message.SignedMessage;
import org.jetbrains.annotations.Nullable;

/**
 * An interface wrapping {@link ArgumentSignatureDataMap} with metadata attached.
 */
public interface SignedCommandArguments {
    /**
     * An empty signed command arguments that always returns {@code null} for
     * {@link #createSignature}.
     */
    public static final SignedCommandArguments EMPTY = new SignedCommandArguments(){

        @Override
        @Nullable
        public SignedMessage createSignature(String argumentName) {
            return null;
        }
    };

    @Nullable
    public SignedMessage createSignature(String var1);

    public record Impl(Map<String, SignedMessage> arguments) implements SignedCommandArguments
    {
        @Override
        @Nullable
        public SignedMessage createSignature(String argumentName) {
            return this.arguments.get(argumentName);
        }
    }
}

