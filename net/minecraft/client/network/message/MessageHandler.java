/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network.message;

import com.google.common.collect.Queues;
import com.mojang.authlib.GameProfile;
import java.time.Instant;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.message.MessageTrustStatus;
import net.minecraft.client.report.ChatLog;
import net.minecraft.client.report.ReceivedMessage;
import net.minecraft.network.message.MessageHeader;
import net.minecraft.network.message.MessageMetadata;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * Handles received messages, including chat messages and game messages.
 */
@Environment(value=EnvType.CLIENT)
public class MessageHandler {
    private final MinecraftClient client;
    private final Deque<MessageProcessor> delayedMessages = Queues.newArrayDeque();
    private long chatDelay;
    private long lastProcessTime;

    public MessageHandler(MinecraftClient client) {
        this.client = client;
    }

    /**
     * Processes all delayed messages until one of them fails to process if the delay
     * has passed, and otherwise does nothing.
     */
    public void processDelayedMessages() {
        if (this.chatDelay == 0L) {
            return;
        }
        if (Util.getMeasuringTimeMs() >= this.lastProcessTime + this.chatDelay) {
            MessageProcessor messageProcessor = this.delayedMessages.poll();
            while (messageProcessor != null && !messageProcessor.process()) {
                messageProcessor = this.delayedMessages.poll();
            }
        }
    }

    /**
     * Sets the chat delay to {@code chatDelay} seconds. If the chat delay was changed
     * to {@code 0}, this also processes all queued messages.
     */
    public void setChatDelay(double chatDelay) {
        long l = (long)(chatDelay * 1000.0);
        if (l == 0L && this.chatDelay > 0L) {
            this.delayedMessages.forEach(MessageProcessor::process);
            this.delayedMessages.clear();
        }
        this.chatDelay = l;
    }

    /**
     * Processes one delayed message from the queue's beginning.
     */
    public void process() {
        this.delayedMessages.remove().process();
    }

    public Collection<?> getDelayedMessages() {
        return this.delayedMessages;
    }

    public boolean removeDelayedMessage(MessageSignatureData signature) {
        Iterator<MessageProcessor> iterator = this.delayedMessages.iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().getHeaderSignature().equals(signature)) continue;
            iterator.remove();
            return true;
        }
        return false;
    }

    /**
     * {@return if the chat delay is set and the message should be delayed}
     */
    private boolean shouldDelay() {
        return this.chatDelay > 0L && Util.getMeasuringTimeMs() < this.lastProcessTime + this.chatDelay;
    }

    /**
     * Queues {@code processor} during {@linkplain #shouldDelay the chat delay},
     * otherwise runs the processor.
     */
    private void process(MessageProcessor processor) {
        if (this.shouldDelay()) {
            this.delayedMessages.add(processor);
        } else {
            processor.process();
        }
    }

    public void onChatMessage(SignedMessage message, MessageType.Parameters params) {
        boolean bl = this.client.options.getOnlyShowSecureChat().getValue();
        SignedMessage signedMessage = bl ? message.withoutUnsigned() : message;
        Text text = params.applyChatDecoration(signedMessage.getContent());
        MessageMetadata messageMetadata = message.createMetadata();
        if (!messageMetadata.lacksSender()) {
            PlayerListEntry playerListEntry = this.getPlayerListEntry(messageMetadata.sender());
            MessageTrustStatus messageTrustStatus = this.getStatus(message, text, playerListEntry);
            if (bl && messageTrustStatus.isInsecure()) {
                return;
            }
            this.process(new MessageProcessor(message.headerSignature(), () -> this.processChatMessage(params, message, text, playerListEntry, messageTrustStatus)));
        } else {
            this.process(new MessageProcessor(message.headerSignature(), () -> this.processProfilelessMessage(params, signedMessage, text)));
        }
    }

    public void onMessageHeader(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest) {
        this.process(new MessageProcessor(signature, () -> this.processHeader(header, signature, bodyDigest)));
    }

    private boolean processChatMessage(MessageType.Parameters params, SignedMessage message, Text decorated, @Nullable PlayerListEntry senderEntry, MessageTrustStatus trustStatus) {
        if (this.client.shouldBlockMessages(message.createMetadata().sender())) {
            return false;
        }
        MessageIndicator messageIndicator = trustStatus.createIndicator(message);
        this.client.inGameHud.getChatHud().addMessage(decorated, message.headerSignature(), messageIndicator);
        this.narrate(params, message);
        this.addToChatLog(message, params, senderEntry, trustStatus);
        this.lastProcessTime = Util.getMeasuringTimeMs();
        return true;
    }

    private boolean processProfilelessMessage(MessageType.Parameters params, SignedMessage message, Text decorated) {
        this.client.inGameHud.getChatHud().addMessage(decorated, MessageIndicator.system());
        this.narrate(params, message);
        this.addToChatLog(decorated, message.getTimestamp());
        this.lastProcessTime = Util.getMeasuringTimeMs();
        return true;
    }

    private boolean processHeader(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest) {
        PlayerListEntry playerListEntry = this.getPlayerListEntry(header.sender());
        if (playerListEntry != null) {
            playerListEntry.getMessageVerifier().storeHeaderVerification(header, signature, bodyDigest);
        }
        this.headerProcessed(header, signature, bodyDigest);
        return false;
    }

    private void narrate(MessageType.Parameters params, SignedMessage message) {
        this.client.getNarratorManager().narrateChatMessage(() -> params.applyNarrationDecoration(message.getContent()));
    }

    private MessageTrustStatus getStatus(SignedMessage message, Text decorated, @Nullable PlayerListEntry senderEntry) {
        if (this.isAlwaysTrusted(message.createMetadata().sender())) {
            return MessageTrustStatus.SECURE;
        }
        return MessageTrustStatus.getStatus(message, decorated, senderEntry);
    }

    private void addToChatLog(SignedMessage message, MessageType.Parameters params, @Nullable PlayerListEntry senderEntry, MessageTrustStatus trustStatus) {
        GameProfile gameProfile = senderEntry != null ? senderEntry.getProfile() : new GameProfile(message.createMetadata().sender(), params.name().getString());
        ChatLog chatLog = this.client.getAbuseReportContext().chatLog();
        chatLog.add(ReceivedMessage.of(gameProfile, params.name(), message, trustStatus));
    }

    private void headerProcessed(MessageHeader header, MessageSignatureData signatures, byte[] bodyDigest) {
    }

    @Nullable
    private PlayerListEntry getPlayerListEntry(UUID sender) {
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
        return clientPlayNetworkHandler != null ? clientPlayNetworkHandler.getPlayerListEntry(sender) : null;
    }

    public void onGameMessage(Text message, boolean overlay) {
        if (this.client.options.getHideMatchedNames().getValue().booleanValue() && this.client.shouldBlockMessages(this.extractSender(message))) {
            return;
        }
        if (overlay) {
            this.client.inGameHud.setOverlayMessage(message, false);
        } else {
            this.client.inGameHud.getChatHud().addMessage(message, MessageIndicator.system());
            this.addToChatLog(message, Instant.now());
        }
        this.client.getNarratorManager().narrate(message);
    }

    private UUID extractSender(Text text) {
        String string = TextVisitFactory.removeFormattingCodes(text);
        String string2 = StringUtils.substringBetween(string, "<", ">");
        if (string2 == null) {
            return Util.NIL_UUID;
        }
        return this.client.getSocialInteractionsManager().getUuid(string2);
    }

    private void addToChatLog(Text message, Instant timestamp) {
        ChatLog chatLog = this.client.getAbuseReportContext().chatLog();
        chatLog.add(ReceivedMessage.of(message, timestamp));
    }

    private boolean isAlwaysTrusted(UUID sender) {
        if (this.client.isInSingleplayer() && this.client.player != null) {
            UUID uUID = this.client.player.getGameProfile().getId();
            return uUID.equals(sender);
        }
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    record MessageProcessor(MessageSignatureData headerSignature, BooleanSupplier processor) {
        MessageSignatureData getHeaderSignature() {
            return this.headerSignature;
        }

        public boolean process() {
            return this.processor.getAsBoolean();
        }
    }
}

