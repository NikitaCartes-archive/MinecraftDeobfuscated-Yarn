/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.message;

import com.mojang.logging.LogUtils;
import java.time.Instant;
import java.util.UUID;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.network.encryption.Signer;
import net.minecraft.network.message.FilterMask;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageLink;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import net.minecraft.util.TextifiedException;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

/**
 * A class for handling the "message chain".
 * 
 * <p>A message chain (since 1.19.3) is implemented using an integer that is incremented
 * for each message, called "index". {@link MessageLink} represents the link that a
 * particular message has.
 * 
 * <p>Clients signing a message with its preceding message's index is called
 * "packing", and the server creating a signed message with its preceding message's
 * index is called "unpacking". Unpacked messages can then be verified to check the
 * chain's legitimacy.
 * 
 * @see MessageLink
 */
public class MessageChain {
    private static final Logger LOGGER = LogUtils.getLogger();
    @Nullable
    private MessageLink link;

    public MessageChain(UUID sender, UUID sessionId) {
        this.link = MessageLink.of(sender, sessionId);
    }

    public Packer getPacker(Signer signer) {
        return body -> {
            MessageLink messageLink = this.nextLink();
            if (messageLink == null) {
                return null;
            }
            return new MessageSignatureData(signer.sign(updatable -> SignedMessage.update(updatable, messageLink, body)));
        };
    }

    public Unpacker getUnpacker(PlayerPublicKey playerPublicKey) {
        SignatureVerifier signatureVerifier = playerPublicKey.createSignatureInstance();
        return (signature, body) -> {
            MessageLink messageLink = this.nextLink();
            if (messageLink == null) {
                throw new MessageChainException((Text)Text.translatable("chat.disabled.chain_broken"), false);
            }
            if (playerPublicKey.data().isExpired()) {
                throw new MessageChainException((Text)Text.translatable("chat.disabled.expiredProfileKey"), false);
            }
            SignedMessage signedMessage = new SignedMessage(messageLink, signature, body, null, FilterMask.PASS_THROUGH);
            if (!signedMessage.verify(signatureVerifier)) {
                throw new MessageChainException((Text)Text.translatable("multiplayer.disconnect.unsigned_chat"), true);
            }
            if (signedMessage.isExpiredOnServer(Instant.now())) {
                LOGGER.warn("Received expired chat: '{}'. Is the client/server system time unsynchronized?", (Object)body.content());
            }
            return signedMessage;
        };
    }

    @Nullable
    private MessageLink nextLink() {
        MessageLink messageLink = this.link;
        if (messageLink != null) {
            this.link = messageLink.next();
        }
        return messageLink;
    }

    @FunctionalInterface
    public static interface Packer {
        public static final Packer NONE = body -> null;

        @Nullable
        public MessageSignatureData pack(MessageBody var1);
    }

    @FunctionalInterface
    public static interface Unpacker {
        public static Unpacker unsigned(UUID uuid) {
            return (signature, body) -> SignedMessage.ofUnsigned(uuid, body.content());
        }

        public SignedMessage unpack(@Nullable MessageSignatureData var1, MessageBody var2) throws MessageChainException;
    }

    public static class MessageChainException
    extends TextifiedException {
        private final boolean shouldDisconnect;

        public MessageChainException(Text message, boolean shouldDisconnect) {
            super(message);
            this.shouldDisconnect = shouldDisconnect;
        }

        public boolean shouldDisconnect() {
            return this.shouldDisconnect;
        }
    }
}

