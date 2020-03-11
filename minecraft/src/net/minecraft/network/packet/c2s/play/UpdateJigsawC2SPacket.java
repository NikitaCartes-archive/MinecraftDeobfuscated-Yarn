package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class UpdateJigsawC2SPacket implements Packet<ServerPlayPacketListener> {
	private BlockPos pos;
	private Identifier attachmentType;
	private Identifier targetPool;
	private String finalState;

	public UpdateJigsawC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public UpdateJigsawC2SPacket(BlockPos pos, Identifier attachmentType, Identifier targetPool, String finalState) {
		this.pos = pos;
		this.attachmentType = attachmentType;
		this.targetPool = targetPool;
		this.finalState = finalState;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.pos = buf.readBlockPos();
		this.attachmentType = buf.readIdentifier();
		this.targetPool = buf.readIdentifier();
		this.finalState = buf.readString(32767);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeBlockPos(this.pos);
		buf.writeIdentifier(this.attachmentType);
		buf.writeIdentifier(this.targetPool);
		buf.writeString(this.finalState);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
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
