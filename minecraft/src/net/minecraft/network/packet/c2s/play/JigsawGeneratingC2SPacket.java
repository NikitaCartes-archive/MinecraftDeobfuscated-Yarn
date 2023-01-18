package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;

public class JigsawGeneratingC2SPacket implements Packet<ServerPlayPacketListener> {
	private final BlockPos pos;
	private final int maxDepth;
	private final boolean keepJigsaws;

	public JigsawGeneratingC2SPacket(BlockPos pos, int maxDepth, boolean keepJigsaws) {
		this.pos = pos;
		this.maxDepth = maxDepth;
		this.keepJigsaws = keepJigsaws;
	}

	public JigsawGeneratingC2SPacket(PacketByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.maxDepth = buf.readVarInt();
		this.keepJigsaws = buf.readBoolean();
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
