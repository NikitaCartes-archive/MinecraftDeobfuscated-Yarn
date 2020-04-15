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

	public JigsawGeneratingC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public JigsawGeneratingC2SPacket(BlockPos pos, int maxDepth) {
		this.pos = pos;
		this.maxDepth = maxDepth;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.pos = buf.readBlockPos();
		this.maxDepth = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeBlockPos(this.pos);
		buf.writeVarInt(this.maxDepth);
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
}
