/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.message;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.network.message.MessageMetadata;
import net.minecraft.network.message.MessageSourceProfile;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.DynamicRegistryManager;

/**
 * A class wrapping {@link SignedMessage} on the server to allow custom behavior for
 * sending messages.
 */
public interface SentMessage {
    /**
     * {@return the wrapped message}
     */
    public SignedMessage getWrappedMessage();

    /**
     * {@return the chat message packet to be sent}
     */
    public ChatMessageS2CPacket toPacket(ServerPlayerEntity var1, MessageType.Parameters var2);

    /**
     * Called after sending the message to applicable clients.
     * 
     * @apiNote This is used to send the message header to clients that didn't receive
     * the message due to text filtering.
     * 
     * @see PlayerManager#sendMessageHeader
     */
    public void afterPacketsSent(PlayerManager var1);

    /**
     * {@return the wrapped {@code message}}
     * 
     * @param profile the original source of the message
     */
    public static SentMessage of(SignedMessage message, MessageSourceProfile profile) {
        if (message.createMetadata().lacksSender()) {
            return new Profileless(message);
        }
        if (!message.createMetadata().sender().equals(profile.profileId())) {
            return new Entity(message);
        }
        return new Chat(message);
    }

    /**
     * {@return the wrapped {@code message}}
     */
    public static FilteredMessage<SentMessage> of(FilteredMessage<SignedMessage> message, MessageSourceProfile profile) {
        return message.mapParts(rawMessage -> SentMessage.of((SignedMessage)message.raw(), profile), Profileless::new);
    }

    public static class Profileless
    implements SentMessage {
        private final SignedMessage message;

        public Profileless(SignedMessage message) {
            this.message = message;
        }

        @Override
        public SignedMessage getWrappedMessage() {
            return this.message;
        }

        @Override
        public ChatMessageS2CPacket toPacket(ServerPlayerEntity player, MessageType.Parameters params) {
            DynamicRegistryManager dynamicRegistryManager = player.world.getRegistryManager();
            MessageType.Serialized serialized = params.toSerialized(dynamicRegistryManager);
            return new ChatMessageS2CPacket(this.message, serialized);
        }

        @Override
        public void afterPacketsSent(PlayerManager playerManager) {
        }
    }

    public static class Entity
    implements SentMessage {
        private final SignedMessage originalMessage;
        private final SignedMessage messageWithoutMetadata;

        public Entity(SignedMessage originalMessage) {
            this.originalMessage = originalMessage;
            this.messageWithoutMetadata = SignedMessage.ofUnsigned(MessageMetadata.of(), originalMessage.getContent());
        }

        @Override
        public SignedMessage getWrappedMessage() {
            return this.originalMessage;
        }

        @Override
        public ChatMessageS2CPacket toPacket(ServerPlayerEntity player, MessageType.Parameters params) {
            DynamicRegistryManager dynamicRegistryManager = player.world.getRegistryManager();
            MessageType.Serialized serialized = params.toSerialized(dynamicRegistryManager);
            return new ChatMessageS2CPacket(this.messageWithoutMetadata, serialized);
        }

        @Override
        public void afterPacketsSent(PlayerManager playerManager) {
            playerManager.sendMessageHeader(this.originalMessage, Set.of());
        }
    }

    public static class Chat
    implements SentMessage {
        private final SignedMessage message;
        private final Set<ServerPlayerEntity> recipients = Sets.newIdentityHashSet();

        public Chat(SignedMessage message) {
            this.message = message;
        }

        @Override
        public SignedMessage getWrappedMessage() {
            return this.message;
        }

        @Override
        public ChatMessageS2CPacket toPacket(ServerPlayerEntity player, MessageType.Parameters params) {
            this.recipients.add(player);
            DynamicRegistryManager dynamicRegistryManager = player.world.getRegistryManager();
            MessageType.Serialized serialized = params.toSerialized(dynamicRegistryManager);
            return new ChatMessageS2CPacket(this.message, serialized);
        }

        @Override
        public void afterPacketsSent(PlayerManager playerManager) {
            playerManager.sendMessageHeader(this.message, this.recipients);
        }
    }
}

