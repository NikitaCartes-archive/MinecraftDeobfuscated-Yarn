package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PlayerActionC2SPacket implements Packet<ServerPlayPacketListener> {
	private BlockPos pos;
	private Direction direction;
	private PlayerActionC2SPacket.Action action;

	public PlayerActionC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public PlayerActionC2SPacket(PlayerActionC2SPacket.Action action, BlockPos blockPos, Direction direction) {
		this.action = action;
		this.pos = blockPos;
		this.direction = direction;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.action = packetByteBuf.readEnumConstant(PlayerActionC2SPacket.Action.class);
		this.pos = packetByteBuf.readBlockPos();
		this.direction = Direction.byId(packetByteBuf.readUnsignedByte());
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.action);
		packetByteBuf.writeBlockPos(this.pos);
		packetByteBuf.writeByte(this.direction.getId());
	}

	public void method_12361(ServerPlayPacketListener serverPlayPacketListener) {
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
		SWAP_HELD_ITEMS;
	}
}
