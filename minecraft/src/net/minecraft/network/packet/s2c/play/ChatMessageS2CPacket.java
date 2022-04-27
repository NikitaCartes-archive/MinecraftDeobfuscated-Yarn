package net.minecraft.network.packet.s2c.play;

import java.security.GeneralSecurityException;
import java.security.Signature;
import java.time.Duration;
import java.time.Instant;
import net.minecraft.network.ChatMessageSender;
import net.minecraft.network.MessageType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.Text;

/**
 * A packet used to send a chat message to the clients.
 * 
 * <p>The content is not wrapped in any way (e.g. by {@code chat.type.text} text); the
 * raw message content is sent to the clients, and they will wrap it (see {@link
 * net.minecraft.client.gui.hud.ChatHudListener#format}.) If custom formats are needed,
 * either send it as a {@link GameMessageS2CPacket game message} or use a server resource pack.
 * 
 * <p>Messages that took more than {@link #TIME_TO_LIVE} to reach the clients are
 * considered expired. This is measured from the time the client sent the chat message
 * to the server. Note that unlike {@link
 * net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket} expired messages are not
 * discarded by the clients; they instead log a warning.
 * 
 * <p>Chat messages have signatures. It is possible to use a bogus signature - such as
 * {@link NetworkEncryptionUtils.SignatureData#NONE} - to send a chat message; however
 * if the signature is invalid (e.g. because the text's content differs from the one
 * sent by the client, or because the passed signature is invalid) the client will
 * log a warning. See {@link NetworkEncryptionUtils#updateSignature} for how the message
 * is signed.
 * 
 * @see net.minecraft.server.network.ServerPlayerEntity#sendChatMessage
 * @see net.minecraft.client.network.ClientPlayNetworkHandler#onChatMessage
 */
public record ChatMessageS2CPacket(
	Text content, MessageType type, ChatMessageSender sender, Instant timeStamp, NetworkEncryptionUtils.SignatureData saltSignature
) implements Packet<ClientPlayPacketListener> {
	private static final Duration TIME_TO_LIVE = ChatMessageC2SPacket.TIME_TO_LIVE.plus(Duration.ofMinutes(2L));

	public ChatMessageS2CPacket(PacketByteBuf buf) {
		this(
			buf.readText(),
			MessageType.byId(buf.readByte()),
			new ChatMessageSender(buf),
			Instant.ofEpochSecond(buf.readLong()),
			new NetworkEncryptionUtils.SignatureData(buf)
		);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeText(this.content);
		buf.writeByte(this.type.getId());
		this.sender.write(buf);
		buf.writeLong(this.timeStamp.getEpochSecond());
		this.saltSignature.write(buf);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChatMessage(this);
	}

	@Override
	public boolean isWritingErrorSkippable() {
		return true;
	}

	/**
	 * {@return whether the chat message has a valid signature}
	 * 
	 * @param publicKey the sender's public key to use for verifying
	 */
	public boolean isSignatureValid(PlayerPublicKey.PublicKeyData publicKey) {
		try {
			Signature signature = publicKey.createSignatureInstance();
			NetworkEncryptionUtils.updateSignature(signature, this.saltSignature.salt(), this.sender.uuid(), this.timeStamp, this.content.getString());
			return signature.verify(this.saltSignature.signature());
		} catch (NetworkEncryptionException | GeneralSecurityException var3) {
			return false;
		}
	}

	/**
	 * {@return when the message is considered expired}
	 */
	private Instant getExpiryTime() {
		return this.timeStamp.plus(TIME_TO_LIVE);
	}

	/**
	 * {@return whether the message is considered expired}
	 */
	public boolean isExpired(Instant currentTime) {
		return currentTime.isAfter(this.getExpiryTime());
	}
}
