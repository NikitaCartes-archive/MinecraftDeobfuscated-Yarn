package net.minecraft.network.packet.s2c.play;

import com.mojang.logging.LogUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;

public record PlayerActionResponseS2CPacket(BlockPos pos, BlockState state, PlayerActionC2SPacket.Action action, boolean approved)
	implements Packet<ClientPlayPacketListener> {
	private static final Logger LOGGER = LogUtils.getLogger();

	public PlayerActionResponseS2CPacket(BlockPos pos, BlockState state, PlayerActionC2SPacket.Action action, boolean approved, String reason) {
		this(pos, state, action, approved);
	}

	public PlayerActionResponseS2CPacket(BlockPos pos, BlockState state, PlayerActionC2SPacket.Action action, boolean approved) {
		pos = pos.toImmutable();
		this.pos = pos;
		this.state = state;
		this.action = action;
		this.approved = approved;
	}

	public PlayerActionResponseS2CPacket(PacketByteBuf buf) {
		this(buf.readBlockPos(), Block.STATE_IDS.get(buf.readVarInt()), buf.readEnumConstant(PlayerActionC2SPacket.Action.class), buf.readBoolean());
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
}
