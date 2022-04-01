package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PlayerActionC2SPacket implements Packet<ServerPlayPacketListener> {
	private final BlockPos pos;
	private final Direction direction;
	private final PlayerActionC2SPacket.Action action;

	public PlayerActionC2SPacket(PlayerActionC2SPacket.Action action, BlockPos pos, Direction direction) {
		this.action = action;
		this.pos = pos.toImmutable();
		this.direction = direction;
	}

	public PlayerActionC2SPacket(PacketByteBuf buf) {
		this.action = buf.readEnumConstant(PlayerActionC2SPacket.Action.class);
		this.pos = buf.readBlockPos();
		this.direction = Direction.byId(buf.readUnsignedByte());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeEnumConstant(this.action);
		buf.writeBlockPos(this.pos);
		buf.writeByte(this.direction.getId());
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPlayerAction(this);
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public Direction getDirection() {
		return this.direction;
	}

	public PlayerActionC2SPacket.Action getAction() {
		return this.action;
	}

	public static enum Action {
		START_DESTROY_BLOCK,
		ABORT_DESTROY_BLOCK,
		STOP_DESTROY_BLOCK,
		DROP_ALL_ITEMS,
		DROP_ITEM,
		RELEASE_USE_ITEM,
		SWAP_ITEM_WITH_OFFHAND;
	}
}
