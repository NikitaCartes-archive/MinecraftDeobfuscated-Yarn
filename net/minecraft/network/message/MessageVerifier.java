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
    public static MessageVerifier create(@Nullable PlayerPublicKey publicKey, boolean bl) {
        if (publicKey != null) {
            return new Impl(publicKey.createSignatureInstance());
        }
        return new class_7651(bl);
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

        private boolean verifyPrecedingSignature(MessageHeader header, MessageSignatureData messageSignatureData, boolean bl) {
            if (messageSignatureData.isEmpty()) {
                return false;
            }
            if (bl && messageSignatureData.equals(this.precedingSignature)) {
                return true;
            }
            return this.precedingSignature == null || this.precedingSignature.equals(header.precedingSignature());
        }

        private boolean verifyInternal(MessageHeader header, MessageSignatureData messageSignatureData, byte[] bodyDigest, boolean bl) {
            return this.verifyPrecedingSignature(header, messageSignatureData, bl) && messageSignatureData.verify(this.signatureVerifier, header, bodyDigest);
        }

        private Status getStatus(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest, boolean bl) {
            boolean bl2 = this.lastMessageVerified = this.lastMessageVerified && this.verifyInternal(header, signature, bodyDigest, bl);
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

    public static class class_7651
    implements MessageVerifier {
        private final boolean field_39952;

        public class_7651(boolean bl) {
            this.field_39952 = bl;
        }

        private Status method_45102(MessageSignatureData messageSignatureData) {
            if (!messageSignatureData.isEmpty()) {
                return Status.BROKEN_CHAIN;
            }
            return this.field_39952 ? Status.BROKEN_CHAIN : Status.NOT_SECURE;
        }

        @Override
        public Status verify(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest) {
            return this.method_45102(signature);
        }

        @Override
        public Status verify(SignedMessage message) {
            return this.method_45102(message.headerSignature());
        }
    }

    public static enum Status {
        SECURE,
        NOT_SECURE,
        BROKEN_CHAIN;

    }
}

