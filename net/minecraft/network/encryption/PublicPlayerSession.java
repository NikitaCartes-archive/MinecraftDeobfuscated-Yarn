/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.encryption;

import com.mojang.authlib.GameProfile;
import java.time.Duration;
import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.network.message.MessageChain;
import net.minecraft.network.message.MessageVerifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public record PublicPlayerSession(UUID sessionId, @Nullable PlayerPublicKey publicKeyData) {
    public static final PublicPlayerSession MISSING = new PublicPlayerSession(Util.NIL_UUID, null);

    public MessageVerifier createVerifier() {
        if (this.publicKeyData != null) {
            return new MessageVerifier.Impl(this.publicKeyData.createSignatureInstance());
        }
        return MessageVerifier.NO_SIGNATURE;
    }

    public MessageChain.Unpacker createUnpacker(UUID sender) {
        if (this.publicKeyData != null) {
            return new MessageChain(sender, this.sessionId).getUnpacker(this.publicKeyData);
        }
        return MessageChain.Unpacker.unsigned(sender);
    }

    public Serialized toSerialized() {
        return new Serialized(this.sessionId, Util.map(this.publicKeyData, PlayerPublicKey::data));
    }

    public boolean hasPublicKey() {
        return this.publicKeyData != null;
    }

    @Nullable
    public PlayerPublicKey publicKeyData() {
        return this.publicKeyData;
    }

    public record Serialized(UUID sessionId, @Nullable PlayerPublicKey.PublicKeyData publicKeyData) {
        public static final Serialized MISSING = MISSING.toSerialized();

        public static Serialized fromBuf(PacketByteBuf buf) {
            return new Serialized(buf.readUuid(), (PlayerPublicKey.PublicKeyData)buf.readNullable(PlayerPublicKey.PublicKeyData::new));
        }

        public static void write(PacketByteBuf buf, Serialized serialized) {
            buf.writeUuid(serialized.sessionId);
            buf.writeNullable(serialized.publicKeyData, (buf2, publicKeyData) -> publicKeyData.write((PacketByteBuf)buf2));
        }

        public PublicPlayerSession toSession(GameProfile gameProfile, SignatureVerifier servicesSignatureVerifier, Duration gracePeriod) throws PlayerPublicKey.PublicKeyException {
            if (this.publicKeyData == null) {
                return MISSING;
            }
            return new PublicPlayerSession(this.sessionId, PlayerPublicKey.verifyAndDecode(servicesSignatureVerifier, gameProfile.getId(), this.publicKeyData, gracePeriod));
        }

        @Nullable
        public PlayerPublicKey.PublicKeyData publicKeyData() {
            return this.publicKeyData;
        }
    }
}

