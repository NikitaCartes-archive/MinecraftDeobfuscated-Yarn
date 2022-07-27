/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.message;

import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.network.message.MessageHeader;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.SignedMessage;
import org.jetbrains.annotations.Nullable;

/**
 * Verifies incoming messages' signature and the message chain.
 * 
 * <p>Methods in this interface must be called in the order of the message's reception,
 * as it affects the verification result.
 */
public interface MessageVerifier {
    public static MessageVerifier create(@Nullable PlayerPublicKey publicKey, boolean secureChatEnforced) {
        if (publicKey != null) {
            return new Impl(publicKey.createSignatureInstance());
        }
        return new Unsigned(secureChatEnforced);
    }

    /**
     * {@return the status of verifying the header}
     * 
     * <p>Clients can receive only the message header instead of the whole message. This
     * allows the verification of such messages.
     */
    public Status verify(MessageHeader var1, MessageSignatureData var2, byte[] var3);

    /**
     * {@return the status of verifying the message}
     */
    public Status verify(SignedMessage var1);

    public static class Impl
    implements MessageVerifier {
        private final SignatureVerifier signatureVerifier;
        @Nullable
        private MessageSignatureData precedingSignature;
        private boolean lastMessageVerified = true;

        public Impl(SignatureVerifier signatureVerifier) {
            this.signatureVerifier = signatureVerifier;
        }

        private boolean verifyPrecedingSignature(MessageHeader header, MessageSignatureData signature, boolean fullMessage) {
            if (signature.isEmpty()) {
                return false;
            }
            if (fullMessage && signature.equals(this.precedingSignature)) {
                return true;
            }
            return this.precedingSignature == null || this.precedingSignature.equals(header.precedingSignature());
        }

        private boolean verifyInternal(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest, boolean fullMessage) {
            return this.verifyPrecedingSignature(header, signature, fullMessage) && signature.verify(this.signatureVerifier, header, bodyDigest);
        }

        private Status getStatus(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest, boolean fullMessage) {
            boolean bl = this.lastMessageVerified = this.lastMessageVerified && this.verifyInternal(header, signature, bodyDigest, fullMessage);
            if (!this.lastMessageVerified) {
                return Status.BROKEN_CHAIN;
            }
            this.precedingSignature = signature;
            return Status.SECURE;
        }

        @Override
        public Status verify(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest) {
            return this.getStatus(header, signature, bodyDigest, false);
        }

        @Override
        public Status verify(SignedMessage message) {
            byte[] bs = message.signedBody().digest().asBytes();
            return this.getStatus(message.signedHeader(), message.headerSignature(), bs, true);
        }
    }

    public static class Unsigned
    implements MessageVerifier {
        private final boolean secureChatEnforced;

        public Unsigned(boolean secureChatEnforced) {
            this.secureChatEnforced = secureChatEnforced;
        }

        private Status getStatus(MessageSignatureData signature) {
            if (!signature.isEmpty()) {
                return Status.BROKEN_CHAIN;
            }
            return this.secureChatEnforced ? Status.BROKEN_CHAIN : Status.NOT_SECURE;
        }

        @Override
        public Status verify(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest) {
            return this.getStatus(signature);
        }

        @Override
        public Status verify(SignedMessage message) {
            return this.getStatus(message.headerSignature());
        }
    }

    public static enum Status {
        SECURE,
        NOT_SECURE,
        BROKEN_CHAIN;

    }
}

