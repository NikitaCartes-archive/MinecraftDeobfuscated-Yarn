package net.minecraft.network.packet.c2s.play;

import java.time.Instant;
import java.util.UUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.ArgumentSignatures;
import net.minecraft.network.encryption.CommandArgumentSigner;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.StringHelper;

/**
 * A packet used to execute commands on the server.
 * 
 * <p>This truncates the command to at most {@value #MAX_COMMAND_LENGTH} characters
 * before sending to the server on the client. If the server receives the command
 * longer than {@value #MAX_COMMAND_LENGTH} characters, it will reject the message
 * and disconnect the client.
 * 
 * <p>If the command contains an invalid character (see {@link
 * net.minecraft.SharedConstants#isValidChar isValidChar}), the server will
 * reject the command and disconnect the client.
 * 
 * <p>Commands that took more than {@link ChatMessageC2SPacket#TIME_TO_LIVE} to reach
 * the server are considered expired and will be discarded.
 * 
 * @see net.minecraft.client.network.ClientPlayerEntity#sendCommand
 * @see net.minecraft.server.network.ServerPlayNetworkHandler#onCommandExecution
 */
public record CommandExecutionC2SPacket(String command, Instant time, ArgumentSignatures argumentSignatures) implements Packet<ServerPlayPacketListener> {
	private static final int MAX_COMMAND_LENGTH = 256;

	public CommandExecutionC2SPacket(String command, Instant time, ArgumentSignatures argumentSignatures) {
		this.command = StringHelper.truncateChat(command);
		this.time = time;
		this.argumentSignatures = argumentSignatures;
	}

	public CommandExecutionC2SPacket(PacketByteBuf buf) {
		this(buf.readString(256), Instant.ofEpochSecond(buf.readLong()), new ArgumentSignatures(buf));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(this.command);
		buf.writeLong(this.time.getEpochSecond());
		this.argumentSignatures.write(buf);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onCommandExecution(this);
	}

	/**
	 * {@return when the command execution is considered expired and should be discarded}
	 */
	private Instant getExpiryTime() {
		return this.time.plus(ChatMessageC2SPacket.TIME_TO_LIVE);
	}

	/**
	 * {@return whether the command execution is considered expired and should be discarded}
	 */
	public boolean isExpired(Instant currentTime) {
		return currentTime.isAfter(this.getExpiryTime());
	}

	public CommandArgumentSigner createArgumentsSigner(UUID sender) {
		return new CommandArgumentSigner.Signatures(sender, this.time, this.argumentSignatures);
	}
}
