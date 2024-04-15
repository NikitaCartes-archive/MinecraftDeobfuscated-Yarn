package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record CommandExecutionC2SPacket(String command) implements Packet<ServerPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, CommandExecutionC2SPacket> CODEC = Packet.createCodec(
		CommandExecutionC2SPacket::write, CommandExecutionC2SPacket::new
	);

	private CommandExecutionC2SPacket(PacketByteBuf buf) {
		this(buf.readString());
	}

	private void write(PacketByteBuf buf) {
		buf.writeString(this.command);
	}

	@Override
	public PacketType<CommandExecutionC2SPacket> getPacketId() {
		return PlayPackets.CHAT_COMMAND;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onCommandExecution(this);
	}
}
