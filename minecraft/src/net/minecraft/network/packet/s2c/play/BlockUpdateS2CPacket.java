package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class BlockUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private final BlockPos pos;
	private final BlockState state;

	public BlockUpdateS2CPacket(BlockPos blockPos, BlockState blockState) {
		this.pos = blockPos;
		this.state = blockState;
	}

	public BlockUpdateS2CPacket(BlockView world, BlockPos pos) {
		this(pos, world.getBlockState(pos));
	}

	public BlockUpdateS2CPacket(PacketByteBuf packetByteBuf) {
		this.pos = packetByteBuf.readBlockPos();
		this.state = Block.STATE_IDS.get(packetByteBuf.readVarInt());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);
		buf.writeVarInt(Block.getRawIdFromState(this.state));
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onBlockUpdate(this);
	}

	@Environment(EnvType.CLIENT)
	public BlockState getState() {
		return this.state;
	}

	@Environment(EnvType.CLIENT)
	public BlockPos getPos() {
		return this.pos;
	}
}
