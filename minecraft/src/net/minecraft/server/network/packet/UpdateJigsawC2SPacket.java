package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class UpdateJigsawC2SPacket implements Packet<ServerPlayPacketListener> {
	private BlockPos pos;
	private Identifier attachmentType;
	private Identifier targetPool;
	private String finalState;

	public UpdateJigsawC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public UpdateJigsawC2SPacket(BlockPos blockPos, Identifier identifier, Identifier identifier2, String string) {
		this.pos = blockPos;
		this.attachmentType = identifier;
		this.targetPool = identifier2;
		this.finalState = string;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.pos = packetByteBuf.readBlockPos();
		this.attachmentType = packetByteBuf.readIdentifier();
		this.targetPool = packetByteBuf.readIdentifier();
		this.finalState = packetByteBuf.readString(32767);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeBlockPos(this.pos);
		packetByteBuf.writeIdentifier(this.attachmentType);
		packetByteBuf.writeIdentifier(this.targetPool);
		packetByteBuf.writeString(this.finalState);
	}

	public void method_16392(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onJigsawUpdate(this);
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public Identifier getTargetPool() {
		return this.targetPool;
	}

	public Identifier getAttachmentType() {
		return this.attachmentType;
	}

	public String getFinalState() {
		return this.finalState;
	}
}
