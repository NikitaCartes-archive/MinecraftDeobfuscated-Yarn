package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.math.BlockPos;

public class JigsawGeneratingC2SPacket implements Packet<ServerPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, JigsawGeneratingC2SPacket> CODEC = Packet.createCodec(
		JigsawGeneratingC2SPacket::write, JigsawGeneratingC2SPacket::new
	);
	private final BlockPos pos;
	private final int maxDepth;
	private final boolean keepJigsaws;

	public JigsawGeneratingC2SPacket(BlockPos pos, int maxDepth, boolean keepJigsaws) {
		this.pos = pos;
		this.maxDepth = maxDepth;
		this.keepJigsaws = keepJigsaws;
	}

	private JigsawGeneratingC2SPacket(PacketByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.maxDepth = buf.readVarInt();
		this.keepJigsaws = buf.readBoolean();
	}

	private void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);
		buf.writeVarInt(this.maxDepth);
		buf.writeBoolean(this.keepJigsaws);
	}

	@Override
	public PacketType<JigsawGeneratingC2SPacket> getPacketId() {
		return PlayPackets.JIGSAW_GENERATE;
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
