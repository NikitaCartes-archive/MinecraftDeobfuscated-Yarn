/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.encryption;

import java.util.UUID;
import net.minecraft.network.encryption.PlayerKeyPair;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.encryption.Signer;
import net.minecraft.network.message.MessageChain;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public record ClientPlayerSession(UUID sessionId, @Nullable PlayerKeyPair keyPair) {
    public static ClientPlayerSession create(@Nullable PlayerKeyPair keyPair) {
        return new ClientPlayerSession(UUID.randomUUID(), keyPair);
    }

    public MessageChain.Packer createPacker(UUID sender) {
        Signer signer = this.createSigner();
        if (signer != null) {
            return new MessageChain(sender, this.sessionId).getPacker(signer);
        }
        return MessageChain.Packer.NONE;
    }

    @Nullable
    public Signer createSigner() {
        if (this.keyPair != null) {
            return Signer.create(this.keyPair.privateKey(), "SHA256withRSA");
        }
        return null;
    }

    public PublicPlayerSession toPublicSession() {
        return new PublicPlayerSession(this.sessionId, Util.map(this.keyPair, PlayerKeyPair::publicKey));
    }

    @Nullable
    public PlayerKeyPair keyPair() {
        return this.keyPair;
    }
}

