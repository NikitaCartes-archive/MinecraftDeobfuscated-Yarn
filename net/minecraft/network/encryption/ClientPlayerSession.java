/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.encryption;

import java.util.UUID;
import net.minecraft.network.encryption.PlayerKeyPair;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.encryption.Signer;
import net.minecraft.network.message.MessageChain;

public record ClientPlayerSession(UUID sessionId, PlayerKeyPair keyPair) {
    public static ClientPlayerSession create(PlayerKeyPair keyPair) {
        return new ClientPlayerSession(UUID.randomUUID(), keyPair);
    }

    public MessageChain.Packer createPacker(UUID sender) {
        return new MessageChain(sender, this.sessionId).getPacker(Signer.create(this.keyPair.privateKey(), "SHA256withRSA"));
    }

    public PublicPlayerSession toPublicSession() {
        return new PublicPlayerSession(this.sessionId, this.keyPair.publicKey());
    }
}

