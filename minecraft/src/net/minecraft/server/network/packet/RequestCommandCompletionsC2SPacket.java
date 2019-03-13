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
	public RequestCommandCompletionsC2SPacket(int i, String string) {
		this.completionId = i;
		this.partialCommand = string;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.completionId = packetByteBuf.readVarInt();
		this.partialCommand = packetByteBuf.readString(32500);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.completionId);
		packetByteBuf.writeString(this.partialCommand, 32500);
	}

	public void method_12147(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12059(this);
	}

	public int getCompletionId() {
		return this.completionId;
	}

	public String getPartialCommand() {
		return this.partialCommand;
	}
}
