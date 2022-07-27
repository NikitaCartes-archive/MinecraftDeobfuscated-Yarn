/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.report.log;

import com.mojang.authlib.GameProfile;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.message.MessageTrustStatus;
import net.minecraft.client.report.log.ChatLogEntry;
import net.minecraft.client.report.log.HeaderEntry;
import net.minecraft.network.message.MessageHeader;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * A message received by the client and stored in {@link ChatLog}.
 * 
 * <p>This includes both {@linkplain net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket
 * chat messages} and {@linkplain net.minecraft.network.packet.s2c.play.GameMessageS2CPacket
 * game messages}.
 */
@Environment(value=EnvType.CLIENT)
public interface ReceivedMessage
extends ChatLogEntry {
    /**
     * {@return the received message constructed from a chat message's elements}
     * 
     * @param displayName the displayed name of the sender
     * @param message the message content
     * @param gameProfile the game profile of the message's sender
     */
    public static ChatMessage of(GameProfile gameProfile, Text displayName, SignedMessage message, MessageTrustStatus trustStatus) {
        return new ChatMessage(gameProfile, displayName, message, trustStatus);
    }

    /**
     * {@return the received message constructed from a game message's elements}
     * 
     * @param message the message content
     * @param timestamp the timestamp of the message
     */
    public static GameMessage of(Text message, Instant timestamp) {
        return new GameMessage(message, timestamp);
    }

    /**
     * {@return the content of the message}
     * 
     * @implNote If the message is a chat message and it contains an unsigned part, the unsigned
     * part will be returned. Note that in vanilla, unsigned part is stripped prior to
     * construction of the received message instance if the client requires secure chat.
     */
    public Text getContent();

    /**
     * {@return the narration of the message (by default, the content)}
     */
    default public Text getNarration() {
        return this.getContent();
    }

    /**
     * {@return whether the sender's UUID equals {@code uuid}}
     */
    public boolean isSentFrom(UUID var1);

    @Environment(value=EnvType.CLIENT)
    public record ChatMessage(GameProfile profile, Text displayName, SignedMessage message, MessageTrustStatus trustStatus) implements ReceivedMessage,
    HeaderEntry
    {
        private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

        @Override
        public Text getContent() {
            if (!this.message.filterMask().isPassThrough()) {
                Text text = this.message.filterMask().filter(this.message.getSignedContent());
                return Objects.requireNonNullElse(text, ScreenTexts.EMPTY);
            }
            return this.message.getContent();
        }

        @Override
        public Text getNarration() {
            Text text = this.getContent();
            Text text2 = this.getFormattedTimestamp();
            return Text.translatable("gui.chatSelection.message.narrate", this.displayName, text, text2);
        }

        public Text getHeadingText() {
            Text text = this.getFormattedTimestamp();
            return Text.translatable("gui.chatSelection.heading", this.displayName, text);
        }

        private Text getFormattedTimestamp() {
            LocalDateTime localDateTime = LocalDateTime.ofInstant(this.message.getTimestamp(), ZoneOffset.systemDefault());
            return Text.literal(localDateTime.format(DATE_TIME_FORMATTER)).formatted(Formatting.ITALIC, Formatting.GRAY);
        }

        @Override
        public boolean isSentFrom(UUID uuid) {
            return this.message.canVerifyFrom(uuid);
        }

        @Override
        public MessageHeader header() {
            return this.message.signedHeader();
        }

        @Override
        public byte[] bodyDigest() {
            return this.message.signedBody().digest().asBytes();
        }

        @Override
        public MessageSignatureData headerSignature() {
            return this.message.headerSignature();
        }

        public UUID getSenderUuid() {
            return this.profile.getId();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record GameMessage(Text message, Instant timestamp) implements ReceivedMessage
    {
        @Override
        public Text getContent() {
            return this.message;
        }

        @Override
        public boolean isSentFrom(UUID uuid) {
            return false;
        }
    }
}

