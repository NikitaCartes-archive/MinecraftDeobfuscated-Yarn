package net.minecraft.network.packet.c2s.play;

import java.time.Instant;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.message.ArgumentSignatureDataMap;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.packet.Packet;

/**
 * A packet used to execute commands on the server.
 * 
 * <p>This truncates the command to at most 256 characters before sending to the
 * server on the client. If the server receives the command longer than 256 characters,
 * it will reject the message and disconnect the client.
 * 
 * <p>If the command contains an invalid character (see {@link
 * net.minecraft.SharedConstants#isValidChar}) or if the server receives
 * the commands in improper order, the server will reject the command and disconnect
 * the client.
 * 
 * <p>Commands that took more than {@link
 * net.minecraft.network.message.SignedMessage#SERVERBOUND_TIME_TO_LIVE}
 * to reach the server are considered expired and log warnings on the server
 * if it contains signed message arguments. If the message takes more than
 * {@link net.minecraft.network.message.SignedMessage#CLIENTBOUND_TIME_TO_LIVE} to
 * reach the clients (including the time it took to reach the server), the message
 * is not considered secure anymore by the clients, and may be discarded depending
 * on the clients' options.
 * 
 * @see net.minecraft.client.network.ClientPlayNetworkHandler#sendCommand(String)
 * @see net.minecraft.server.network.ServerPlayNetworkHandler#onCommandExecution
 */
public record CommandExecutionC2SPacket(
	String command, Instant timestamp, long salt, ArgumentSignatureDataMap argumentSignatures, LastSeenMessageList.Acknowledgment acknowledgment
) implements Packet<ServerPlayPacketListener> {
	public CommandExecutionC2SPacket(PacketByteBuf buf) {
		this(buf.readString(256), buf.readInstant(), buf.readLong(), new ArgumentSignatureDataMap(buf), new LastSeenMessageList.Acknowledgment(buf));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(this.command, 256);
		buf.writeInstant(this.timestamp);
		buf.writeLong(this.salt);
		this.argumentSignatures.write(buf);
		this.acknowledgment.write(buf);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onCommandExecution(this);
	}
}
