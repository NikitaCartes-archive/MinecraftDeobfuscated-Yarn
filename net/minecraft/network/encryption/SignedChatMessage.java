/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.encryption;

import java.security.GeneralSecurityException;
import java.security.Signature;
import java.security.SignatureException;
import net.minecraft.network.encryption.ChatMessageSignature;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.text.Text;

public record SignedChatMessage(Text content, ChatMessageSignature signature) {
    public SignedChatMessage(String content, ChatMessageSignature signature) {
        this(Text.literal(content), signature);
    }

    public boolean verify(Signature signature) throws SignatureException {
        return this.signature.verify(signature, this.content);
    }

    public boolean verify(PlayerPublicKey publicKey) {
        try {
            return this.verify(publicKey.createSignatureInstance());
        } catch (GeneralSecurityException | NetworkEncryptionException exception) {
            return false;
        }
    }
}

