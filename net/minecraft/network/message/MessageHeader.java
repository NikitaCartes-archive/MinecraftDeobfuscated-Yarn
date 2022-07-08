/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.message;

import java.security.SignatureException;
import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.SignatureUpdatable;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import org.jetbrains.annotations.Nullable;

/**
 * The header of a message, which contains the preceding message's signature and the
 * sender's UUID. Unlike {@link MessageBody}, clients receive this even if the message
 * is censored.
 */
public record MessageHeader(@Nullable MessageSignatureData precedingSignature, UUID sender) {
    public MessageHeader(PacketByteBuf buf) {
        this((MessageSignatureData)buf.readNullable(MessageSignatureData::new), buf.readUuid());
    }

    public void write(PacketByteBuf buf) {
        buf.writeNullable(this.precedingSignature, (buf2, precedingSignature) -> precedingSignature.write((PacketByteBuf)buf2));
        buf.writeUuid(this.sender);
    }

    public void update(SignatureUpdatable.SignatureUpdater updater, byte[] bodyDigest) throws SignatureException {
        if (this.precedingSignature != null) {
            updater.update(this.precedingSignature.data());
        }
        updater.update(DynamicSerializableUuid.toByteArray(this.sender));
        updater.update(bodyDigest);
    }

    @Nullable
    public MessageSignatureData precedingSignature() {
        return this.precedingSignature;
    }
}

