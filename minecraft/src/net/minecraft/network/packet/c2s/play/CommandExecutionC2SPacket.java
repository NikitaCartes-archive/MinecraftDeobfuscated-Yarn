package net.minecraft.network.packet.c2s.play;

import java.time.Instant;
import java.util.UUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.message.ArgumentSignatureDataMap;
import net.minecraft.network.message.CommandArgumentSigner;
import net.minecraft.util.StringHelper;

/**
 * A packet used to execute commands on the server.
 * 
 * <p>This truncates the command to at most 256 characters before sending to the
 * server on the client. If the server receives the command longer than 256 characters,
 * it will reject the message and disconnect the client.
 * 
 * <p>If the command contains an invalid character (see {@link
 * net.minecraft.SharedConstants#isValidChar isValidChar}), the server will
 * reject the command and disconnect the client.
 * 
 * <p>Commands that took more than {@link ChatMessageC2SPacket#TIME_TO_LIVE} to reach
 * the server are considered expired and will be discarded. Commands can also be discarded
 * if the server receives them with improper order.
 * 
 * @see net.minecraft.client.network.ClientPlayerEntity#sendCommand
 * @see net.minecraft.server.network.ServerPlayNetworkHandler#onCommandExecution
 */
public record CommandExecutionC2SPacket(String command, Instant timestamp, ArgumentSignatureDataMap argumentSignatures, boolean signedPreview)
	implements Packet<ServerPlayPacketListener> {
	public CommandExecutionC2SPacket(String command, Instant timestamp, ArgumentSignatureDataMap argumentSignatures, boolean signedPreview) {
		command = StringHelper.truncateChat(command);
		this.command = command;
		this.timestamp = timestamp;
		this.argumentSignatures = argumentSignatures;
		this.signedPreview = signedPreview;
	}

	public CommandExecutionC2SPacket(PacketByteBuf buf) {
		this(buf.readString(256), buf.readInstant(), new ArgumentSignatureDataMap(buf), buf.readBoolean());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(this.command, 256);
		buf.writeInstant(this.timestamp);
		this.argumentSignatures.write(buf);
		buf.writeBoolean(this.signedPreview);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onCommandExecution(this);
	}

	public CommandArgumentSigner createArgumentsSigner(UUID sender) {
		return new CommandArgumentSigner.Signatures(sender, this.timestamp, this.argumentSignatures, this.signedPreview);
	}
}
