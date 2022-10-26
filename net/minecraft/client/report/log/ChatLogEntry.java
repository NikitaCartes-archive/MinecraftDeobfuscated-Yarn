/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.report.log;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.report.log.ReceivedMessage;
import net.minecraft.util.StringIdentifiable;

/**
 * An entry logged to {@link ChatLog}.
 */
@Environment(value=EnvType.CLIENT)
public interface ChatLogEntry {
    public static final Codec<ChatLogEntry> CODEC = StringIdentifiable.createCodec(Type::values).dispatch(ChatLogEntry::getType, Type::getCodec);

    public Type getType();

    @Environment(value=EnvType.CLIENT)
    public static enum Type implements StringIdentifiable
    {
        PLAYER("player", () -> ReceivedMessage.ChatMessage.CHAT_MESSAGE_CODEC),
        SYSTEM("system", () -> ReceivedMessage.GameMessage.GAME_MESSAGE_CODEC);

        private final String id;
        private final Supplier<Codec<? extends ChatLogEntry>> codecSupplier;

        private Type(String id, Supplier<Codec<? extends ChatLogEntry>> codecSupplier) {
            this.id = id;
            this.codecSupplier = codecSupplier;
        }

        private Codec<? extends ChatLogEntry> getCodec() {
            return this.codecSupplier.get();
        }

        @Override
        public String asString() {
            return this.id;
        }
    }
}

