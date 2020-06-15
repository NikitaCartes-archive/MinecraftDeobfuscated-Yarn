package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.math.BlockPos;

public class JigsawGeneratingC2SPacket implements Packet<ServerPlayPacketListener> {
	private BlockPos pos;
	private int maxDepth;
	private boolean keepJigsaws;

	public JigsawGeneratingC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public JigsawGeneratingC2SPacket(BlockPos pos, int maxDepth, boolean keepJigsaws) {
		this.pos = pos;
		this.maxDepth = maxDepth;
		this.keepJigsaws = keepJigsaws;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.pos = buf.readBlockPos();
		this.maxDepth = buf.readVarInt();
		this.keepJigsaws = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
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
