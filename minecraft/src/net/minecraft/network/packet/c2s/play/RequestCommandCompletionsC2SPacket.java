package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;

public class RequestCommandCompletionsC2SPacket implements Packet<ServerPlayPacketListener> {
	private final int completionId;
	private final String partialCommand;

	public RequestCommandCompletionsC2SPacket(int completionId, String partialCommand) {
		this.completionId = completionId;
		this.partialCommand = partialCommand;
	}

	public RequestCommandCompletionsC2SPacket(PacketByteBuf buf) {
		this.completionId = buf.readVarInt();
		this.partialCommand = buf.readString(32500);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.completionId);
		buf.writeString(this.partialCommand, 32500);
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
