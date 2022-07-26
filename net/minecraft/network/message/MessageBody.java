/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.message;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.DecoratedContents;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.text.Text;

/**
 * A body of a message, including the content, timestamp, salt used for the digest
 * (the hashed body), and the list of players' "last seen messages". Unlike {@link
 * MessageHeader}, clients do not receive this if the message is censored; they receive
 * the digest only.
 */
public record MessageBody(DecoratedContents content, Instant timestamp, long salt, LastSeenMessageList lastSeenMessages) {
    public static final byte LAST_SEEN_SEPARATOR = 70;

    public MessageBody(PacketByteBuf buf) {
        this(DecoratedContents.read(buf), buf.readInstant(), buf.readLong(), new LastSeenMessageList(buf));
    }

    public void write(PacketByteBuf buf) {
        DecoratedContents.write(buf, this.content);
        buf.writeInstant(this.timestamp);
        buf.writeLong(this.salt);
        this.lastSeenMessages.write(buf);
    }

    /**
     * {@return the digest of this body}
     * 
     * @implNote This is a SHA-256 hash of the salt, the timestamp represented as the seconds
     * since the Unix epoch, the content, and the list of each player's last seen message.
     */
    public HashCode digest() {
        HashingOutputStream hashingOutputStream = new HashingOutputStream(Hashing.sha256(), OutputStream.nullOutputStream());
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(hashingOutputStream);
            dataOutputStream.writeLong(this.salt);
            dataOutputStream.writeLong(this.timestamp.getEpochSecond());
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter((OutputStream)dataOutputStream, StandardCharsets.UTF_8);
            outputStreamWriter.write(this.content.plain());
            outputStreamWriter.flush();
            dataOutputStream.write(70);
            if (this.content.isDecorated()) {
                outputStreamWriter.write(Text.Serializer.toSortedJsonString(this.content.decorated()));
                outputStreamWriter.flush();
            }
            this.lastSeenMessages.write(dataOutputStream);
        } catch (IOException iOException) {
            // empty catch block
        }
        return hashingOutputStream.hash();
    }

    /**
     * {@return a new message body with its content replaced with {@code content}}
     */
    public MessageBody withContent(DecoratedContents content) {
        return new MessageBody(content, this.timestamp, this.salt, this.lastSeenMessages);
    }
}

