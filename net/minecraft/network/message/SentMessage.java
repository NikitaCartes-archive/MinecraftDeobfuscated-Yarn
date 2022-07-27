/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.message;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.MessageHeaderS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.registry.DynamicRegistryManager;

/**
 * A class wrapping {@link SignedMessage} on the server to allow custom behavior for
 * sending messages.
 */
public interface SentMessage {
    public Text getContent();

    public void send(ServerPlayerEntity var1, boolean var2, MessageType.Parameters var3);

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
     */
    public static SentMessage of(SignedMessage message) {
        if (message.createMetadata().lacksSender()) {
            return new Profileless(message);
        }
        return new Chat(message);
    }

    public static class Profileless
    implements SentMessage {
        private final SignedMessage message;

        public Profileless(SignedMessage message) {
            this.message = message;
        }

        @Override
        public Text getContent() {
            return this.message.getContent();
        }

        @Override
        public void send(ServerPlayerEntity sender, boolean filterMaskEnabled, MessageType.Parameters params) {
            SignedMessage signedMessage = this.message.withFilterMaskEnabled(filterMaskEnabled);
            if (!signedMessage.isFullyFiltered()) {
                DynamicRegistryManager dynamicRegistryManager = sender.world.getRegistryManager();
                MessageType.Serialized serialized = params.toSerialized(dynamicRegistryManager);
                sender.networkHandler.sendPacket(new ChatMessageS2CPacket(signedMessage, serialized));
                sender.networkHandler.addPendingAcknowledgment(signedMessage);
            }
        }

        @Override
        public void afterPacketsSent(PlayerManager playerManager) {
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
        public Text getContent() {
            return this.message.getContent();
        }

        @Override
        public void send(ServerPlayerEntity sender, boolean filterMaskEnabled, MessageType.Parameters params) {
            SignedMessage signedMessage = this.message.withFilterMaskEnabled(filterMaskEnabled);
            if (!signedMessage.isFullyFiltered()) {
                this.recipients.add(sender);
                DynamicRegistryManager dynamicRegistryManager = sender.world.getRegistryManager();
                MessageType.Serialized serialized = params.toSerialized(dynamicRegistryManager);
                sender.networkHandler.sendPacket(new ChatMessageS2CPacket(signedMessage, serialized), PacketCallbacks.of(() -> new MessageHeaderS2CPacket(this.message)));
                sender.networkHandler.addPendingAcknowledgment(signedMessage);
            }
        }

        @Override
        public void afterPacketsSent(PlayerManager playerManager) {
            playerManager.sendMessageHeader(this.message, this.recipients);
        }
    }
}

