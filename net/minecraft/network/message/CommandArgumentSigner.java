/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.message;

import java.time.Instant;
import java.util.UUID;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.message.ArgumentSignatureDataMap;
import net.minecraft.network.message.MessageSignature;

/**
 * A signer for command arguments.
 */
public interface CommandArgumentSigner {
    public static final CommandArgumentSigner NONE = argumentName -> MessageSignature.none();

    public MessageSignature getArgumentSignature(String var1);

    default public boolean isPreviewSigned(String argumentName) {
        return false;
    }

    public record Signatures(UUID sender, Instant timestamp, ArgumentSignatureDataMap argumentSignatures, boolean signedPreview) implements CommandArgumentSigner
    {
        @Override
        public MessageSignature getArgumentSignature(String string) {
            NetworkEncryptionUtils.SignatureData signatureData = this.argumentSignatures.get(string);
            if (signatureData != null) {
                return new MessageSignature(this.sender, this.timestamp, signatureData);
            }
            return MessageSignature.none();
        }

        @Override
        public boolean isPreviewSigned(String argumentName) {
            return this.signedPreview;
        }
    }
}

