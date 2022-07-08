/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.message;

import it.unimi.dsi.fastutil.bytes.ByteArrays;
import java.util.Arrays;
import java.util.Base64;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageHeader;
import org.jetbrains.annotations.Nullable;

/**
 * A message signature data that can be verified when given the header.
 */
public record MessageSignatureData(byte[] data) {
    public static final MessageSignatureData EMPTY = new MessageSignatureData(ByteArrays.EMPTY_ARRAY);

    public MessageSignatureData(PacketByteBuf buf) {
        this(buf.readByteArray());
    }

    public void write(PacketByteBuf buf) {
        buf.writeByteArray(this.data);
    }

    /**
     * {@return whether the signature data is verified}
     * 
     * @param verifier the verifier that is created with the sender's public key
     */
    public boolean verify(SignatureVerifier verifier, MessageHeader header, MessageBody body) {
        if (!this.isEmpty()) {
            byte[] bs = body.digest().asBytes();
            return verifier.validate(updatable -> header.update(updatable, bs), this.data);
        }
        return false;
    }

    /**
     * {@return whether the signature data is verified}
     * 
     * @param bodyDigest the {@linkplain MessageBody#digest digest of the message body}
     * @param verifier the verifier that is created with the sender's public key
     */
    public boolean verify(SignatureVerifier verifier, MessageHeader header, byte[] bodyDigest) {
        if (!this.isEmpty()) {
            return verifier.validate(updatable -> header.update(updatable, bodyDigest), this.data);
        }
        return false;
    }

    public boolean isEmpty() {
        return this.data.length == 0;
    }

    /**
     * {@return the base64-encoded data, or {@code null} if the data is empty}
     */
    @Nullable
    public String toStringOrNull() {
        if (!this.isEmpty()) {
            return Base64.getEncoder().encodeToString(this.data);
        }
        return null;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageSignatureData)) return false;
        MessageSignatureData messageSignatureData = (MessageSignatureData)o;
        if (!Arrays.equals(this.data, messageSignatureData.data)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.data);
    }

    @Override
    public String toString() {
        if (!this.isEmpty()) {
            return Base64.getEncoder().encodeToString(this.data);
        }
        return "empty";
    }
}

