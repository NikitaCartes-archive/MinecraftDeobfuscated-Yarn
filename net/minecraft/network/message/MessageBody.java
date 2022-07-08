/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.message;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;

/**
 * A body of a message, including the content, timestamp, salt used for the digest
 * (the hashed body), and the list of players' "last seen messages". Unlike {@link
 * MessageHeader}, clients do not receive this if the message is censored; they receive
 * the digest only.
 */
public record MessageBody(Text content, Instant timestamp, long salt, List<LastSeenMessage> lastSeenMessages) {
    private static final byte LAST_SEEN_SEPARATOR = 70;

    public MessageBody(PacketByteBuf buf) {
        this(buf.readText(), buf.readInstant(), buf.readLong(), buf.readCollection(ArrayList::new, LastSeenMessage::new));
    }

    public void write(PacketByteBuf buf) {
        buf.writeText(this.content);
        buf.writeInstant(this.timestamp);
        buf.writeLong(this.salt);
        buf.writeCollection(this.lastSeenMessages, (buf2, lastSeenMessages) -> lastSeenMessages.write((PacketByteBuf)buf2));
    }

    /**
     * {@return the digest of this body}
     * 
     * @implNote This is a SHA-256 hash of the salt, the timestamp represented as the seconds
     * since the Unix epoch, {@linkplain #toBytes(Text) the content}, and
     * {@linkplain #toBytes(List) the list of each player's last seen message}.
     */
    public HashCode digest() {
        byte[] bs = MessageBody.toBytes(this.content);
        byte[] cs = MessageBody.toBytes(this.lastSeenMessages);
        byte[] ds = new byte[16 + bs.length];
        ByteBuffer byteBuffer = ByteBuffer.wrap(ds).order(ByteOrder.BIG_ENDIAN);
        byteBuffer.putLong(this.salt);
        byteBuffer.putLong(this.timestamp.getEpochSecond());
        byteBuffer.put(bs);
        byteBuffer.put(cs);
        return Hashing.sha256().hashBytes(ds);
    }

    /**
     * {@return {@code content} converted to a byte array}
     * 
     * @implNote This returns the UTF-8 encoded string representing the
     * JSON serialized version of {@code content}.
     */
    private static byte[] toBytes(Text content) {
        String string = Text.Serializer.toSortedJsonString(content);
        return string.getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] toBytes(List<LastSeenMessage> messages) {
        int i = messages.stream().mapToInt(lastSeenMessage -> 17 + lastSeenMessage.lastSignature().data().length).sum();
        byte[] bs = new byte[i];
        ByteBuffer byteBuffer = ByteBuffer.wrap(bs).order(ByteOrder.BIG_ENDIAN);
        for (LastSeenMessage lastSeenMessage2 : messages) {
            UUID uUID = lastSeenMessage2.profileId();
            MessageSignatureData messageSignatureData = lastSeenMessage2.lastSignature();
            byteBuffer.put((byte)70).putLong(uUID.getMostSignificantBits()).putLong(uUID.getLeastSignificantBits()).put(messageSignatureData.data());
        }
        return bs;
    }

    public record LastSeenMessage(UUID profileId, MessageSignatureData lastSignature) {
        public LastSeenMessage(PacketByteBuf buf) {
            this(buf.readUuid(), new MessageSignatureData(buf));
        }

        public void write(PacketByteBuf buf) {
            buf.writeUuid(this.profileId);
            this.lastSignature.write(buf);
        }
    }
}

