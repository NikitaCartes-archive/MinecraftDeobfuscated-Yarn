package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class RequestCommandCompletionsC2SPacket implements Packet<ServerPlayPacketListener> {
	private int completionId;
	private String partialCommand;

	public RequestCommandCompletionsC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public RequestCommandCompletionsC2SPacket(int completionId, String partialCommand) {
		this.completionId = completionId;
		this.partialCommand = partialCommand;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.completionId = buf.readVarInt();
		this.partialCommand = buf.readString(32500);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
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
