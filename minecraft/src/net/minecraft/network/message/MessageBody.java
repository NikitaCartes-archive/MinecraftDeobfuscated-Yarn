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
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(dataOutputStream, StandardCharsets.UTF_8);
			outputStreamWriter.write(this.content.plain());
			outputStreamWriter.flush();
			dataOutputStream.write(70);
			if (this.content.isDecorated()) {
				outputStreamWriter.write(Text.Serializer.toSortedJsonString(this.content.decorated()));
				outputStreamWriter.flush();
			}

			this.lastSeenMessages.write(dataOutputStream);
		} catch (IOException var4) {
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
