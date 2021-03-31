package net.minecraft.network.packet.s2c.play;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerActionResponseS2CPacket implements Packet<ClientPlayPacketListener> {
	private static final Logger LOGGER = LogManager.getLogger();
	private final BlockPos pos;
	private final BlockState state;
	private final PlayerActionC2SPacket.Action action;
	private final boolean approved;

	public PlayerActionResponseS2CPacket(BlockPos pos, BlockState state, PlayerActionC2SPacket.Action action, boolean approved, String reason) {
		this.pos = pos.toImmutable();
		this.state = state;
		this.action = action;
		this.approved = approved;
	}

	public PlayerActionResponseS2CPacket(PacketByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.state = Block.STATE_IDS.get(buf.readVarInt());
		this.action = buf.readEnumConstant(PlayerActionC2SPacket.Action.class);
		this.approved = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);
		buf.writeVarInt(Block.getRawIdFromState(this.state));
		buf.writeEnumConstant(this.action);
		buf.writeBoolean(this.approved);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerActionResponse(this);
	}

	public BlockState getBlockState() {
		return this.state;
	}

	public BlockPos getBlockPos() {
		return this.pos;
	}

	public boolean isApproved() {
		return this.approved;
	}

	public PlayerActionC2SPacket.Action getAction() {
		return this.action;
	}
}
