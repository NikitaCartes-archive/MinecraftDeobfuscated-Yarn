/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.encryption;

import java.time.Instant;
import java.util.UUID;
import net.minecraft.network.encryption.ArgumentSignatureDataMap;
import net.minecraft.network.encryption.ChatMessageSignature;
import net.minecraft.network.encryption.NetworkEncryptionUtils;

/**
 * A signer for command arguments.
 */
public interface CommandArgumentSigner {
    public static final CommandArgumentSigner NONE = argumentName -> ChatMessageSignature.none();

    public ChatMessageSignature getArgumentSignature(String var1);

    default public boolean isPreviewSigned(String argumentName) {
        return false;
    }

    public record Signatures(UUID sender, Instant timestamp, ArgumentSignatureDataMap argumentSignatures, boolean signedPreview) implements CommandArgumentSigner
    {
        @Override
        public ChatMessageSignature getArgumentSignature(String string) {
            NetworkEncryptionUtils.SignatureData signatureData = this.argumentSignatures.get(string);
            if (signatureData != null) {
                return new ChatMessageSignature(this.sender, this.timestamp, signatureData);
            }
            return ChatMessageSignature.none();
        }

        @Override
        public boolean isPreviewSigned(String argumentName) {
            return this.signedPreview;
        }
    }
}

