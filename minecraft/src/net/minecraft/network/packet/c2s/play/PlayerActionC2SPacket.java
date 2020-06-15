package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PlayerActionC2SPacket implements Packet<ServerPlayPacketListener> {
	private BlockPos pos;
	private Direction direction;
	private PlayerActionC2SPacket.Action action;

	public PlayerActionC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public PlayerActionC2SPacket(PlayerActionC2SPacket.Action action, BlockPos pos, Direction direction) {
		this.action = action;
		this.pos = pos.toImmutable();
		this.direction = direction;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.action = buf.readEnumConstant(PlayerActionC2SPacket.Action.class);
		this.pos = buf.readBlockPos();
		this.direction = Direction.byId(buf.readUnsignedByte());
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
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
