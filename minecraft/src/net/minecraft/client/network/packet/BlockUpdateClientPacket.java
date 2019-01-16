package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class BlockUpdateClientPacket implements Packet<ClientPlayPacketListener> {
	private BlockPos pos;
	private BlockState state;

	public BlockUpdateClientPacket() {
	}

	public BlockUpdateClientPacket(BlockView blockView, BlockPos blockPos) {
		this.pos = blockPos;
		this.state = blockView.getBlockState(blockPos);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.pos = packetByteBuf.readBlockPos();
		this.state = Block.STATE_IDS.getInt(packetByteBuf.readVarInt());
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeBlockPos(this.pos);
		packetByteBuf.writeVarInt(Block.getRawIdFromState(this.state));
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
