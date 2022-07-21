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
    public static MessageVerifier method_45057(final class_7646 arg) {
        return new MessageVerifier(){

            @Override
            public class_7646 storeHeaderVerification(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest) {
                return arg;
            }

            @Override
            public class_7646 verify(SignedMessage message) {
                return arg;
            }
        };
    }

    public static MessageVerifier create(@Nullable PlayerPublicKey publicKey, boolean bl) {
        if (publicKey == null) {
            return MessageVerifier.method_45057(bl ? class_7646.BROKEN_CHAIN : class_7646.NOT_SECURE);
        }
        return new Impl(publicKey.createSignatureInstance());
    }

    /**
     * Stores the status of verifying the header.
     * 
     * <p>Clients can receive only the message header instead of the whole message. This
     * allows the chain to reference such messages. Since no actual content is received,
     * this does not return the verification status.
     */
    public class_7646 storeHeaderVerification(MessageHeader var1, MessageSignatureData var2, byte[] var3);

    public class_7646 verify(SignedMessage var1);

    public static enum class_7646 {
        SECURE,
        NOT_SECURE,
        BROKEN_CHAIN;

    }

    public static class Impl
    implements MessageVerifier {
        private final SignatureVerifier signatureVerifier;
        @Nullable
        private MessageSignatureData precedingSignature;
        private boolean lastMessageVerified = true;

        public Impl(SignatureVerifier signatureVerifier) {
            this.signatureVerifier = signatureVerifier;
        }

        private boolean verifyPrecedingSignature(MessageHeader header, MessageSignatureData signature) {
            if (signature.isEmpty()) {
                return false;
            }
            return this.precedingSignature == null || this.precedingSignature.equals(header.precedingSignature()) || this.precedingSignature.equals(signature);
        }

        private boolean verify(MessageHeader messageHeader, MessageSignatureData signature, byte[] bodyDigest) {
            return signature.verify(this.signatureVerifier, messageHeader, bodyDigest);
        }

        private class_7646 method_45048(MessageHeader messageHeader, MessageSignatureData messageSignatureData, byte[] bs) {
            boolean bl = this.lastMessageVerified = this.lastMessageVerified && this.verifyPrecedingSignature(messageHeader, messageSignatureData);
            if (!this.lastMessageVerified) {
                return class_7646.BROKEN_CHAIN;
            }
            if (!this.verify(messageHeader, messageSignatureData, bs)) {
                this.precedingSignature = null;
                return class_7646.NOT_SECURE;
            }
            this.precedingSignature = messageSignatureData;
            return class_7646.SECURE;
        }

        @Override
        public class_7646 storeHeaderVerification(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest) {
            return this.method_45048(header, signature, bodyDigest);
        }

        @Override
        public class_7646 verify(SignedMessage message) {
            byte[] bs = message.signedBody().digest().asBytes();
            return this.method_45048(message.signedHeader(), message.headerSignature(), bs);
        }
    }
}

