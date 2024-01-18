package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class RequestCommandCompletionsC2SPacket implements Packet<ServerPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, RequestCommandCompletionsC2SPacket> CODEC = Packet.createCodec(
		RequestCommandCompletionsC2SPacket::write, RequestCommandCompletionsC2SPacket::new
	);
	private final int completionId;
	private final String partialCommand;

	public RequestCommandCompletionsC2SPacket(int completionId, String partialCommand) {
		this.completionId = completionId;
		this.partialCommand = partialCommand;
	}

	private RequestCommandCompletionsC2SPacket(PacketByteBuf buf) {
		this.completionId = buf.readVarInt();
		this.partialCommand = buf.readString(32500);
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.completionId);
		buf.writeString(this.partialCommand, 32500);
	}

	@Override
	public PacketType<RequestCommandCompletionsC2SPacket> getPacketId() {
		return PlayPackets.COMMAND_SUGGESTION;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onRequestCommandCompletions(this);
	}

	public int getCompletionId() {
		return this.completionId;
	}

	public String getPartialCommand() {
		return this.partialCommand;
	}
}
