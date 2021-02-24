package net.minecraft.network.packet.c2s.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.math.BlockPos;

public class JigsawGeneratingC2SPacket implements Packet<ServerPlayPacketListener> {
	private final BlockPos pos;
	private final int maxDepth;
	private final boolean keepJigsaws;

	@Environment(EnvType.CLIENT)
	public JigsawGeneratingC2SPacket(BlockPos pos, int maxDepth, boolean keepJigsaws) {
		this.pos = pos;
		this.maxDepth = maxDepth;
		this.keepJigsaws = keepJigsaws;
	}

	public JigsawGeneratingC2SPacket(PacketByteBuf packetByteBuf) {
		this.pos = packetByteBuf.readBlockPos();
		this.maxDepth = packetByteBuf.readVarInt();
		this.keepJigsaws = packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);
		buf.writeVarInt(this.maxDepth);
		buf.writeBoolean(this.keepJigsaws);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onJigsawGenerating(this);
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public int getMaxDepth() {
		return this.maxDepth;
	}

	public boolean shouldKeepJigsaws() {
		return this.keepJigsaws;
	}
}
