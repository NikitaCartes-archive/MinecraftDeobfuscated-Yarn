/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.message;

import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.network.message.SignedMessage;
import org.jetbrains.annotations.Nullable;

/**
 * Verifies incoming messages' signature and the message chain.
 * 
 * <p>Methods in this interface must be called in the order of the message's reception,
 * as it affects the verification result.
 */
@FunctionalInterface
public interface MessageVerifier {
    public static final MessageVerifier NO_SIGNATURE = message -> !message.hasSignature();
    public static final MessageVerifier UNVERIFIED = message -> false;

    public boolean isVerified(SignedMessage var1);

    public static class Impl
    implements MessageVerifier {
        private final SignatureVerifier signatureVerifier;
        @Nullable
        private SignedMessage lastVerifiedMessage;
        private boolean lastMessageVerified = true;

        public Impl(SignatureVerifier signatureVerifier) {
            this.signatureVerifier = signatureVerifier;
        }

        private boolean verifyPrecedingSignature(SignedMessage message) {
            if (message.equals(this.lastVerifiedMessage)) {
                return true;
            }
            return this.lastVerifiedMessage == null || message.link().linksTo(this.lastVerifiedMessage.link());
        }

        @Override
        public boolean isVerified(SignedMessage message) {
            boolean bl = this.lastMessageVerified = this.lastMessageVerified && message.verify(this.signatureVerifier) && this.verifyPrecedingSignature(message);
            if (!this.lastMessageVerified) {
                return false;
            }
            this.lastVerifiedMessage = message;
            return true;
        }
    }
}

