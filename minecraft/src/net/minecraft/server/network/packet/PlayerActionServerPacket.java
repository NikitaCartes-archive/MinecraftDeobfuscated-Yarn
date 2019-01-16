package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PlayerActionServerPacket implements Packet<ServerPlayPacketListener> {
	private BlockPos pos;
	private Direction direction;
	private PlayerActionServerPacket.Action action;

	public PlayerActionServerPacket() {
	}

	@Environment(EnvType.CLIENT)
	public PlayerActionServerPacket(PlayerActionServerPacket.Action action, BlockPos blockPos, Direction direction) {
		this.action = action;
		this.pos = blockPos;
		this.direction = direction;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.action = packetByteBuf.readEnumConstant(PlayerActionServerPacket.Action.class);
		this.pos = packetByteBuf.readBlockPos();
		this.direction = Direction.byId(packetByteBuf.readUnsignedByte());
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.action);
		packetByteBuf.writeBlockPos(this.pos);
		packetByteBuf.writeByte(this.direction.getId());
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

	public PlayerActionServerPacket.Action getAction() {
		return this.action;
	}

	public static enum Action {
		field_12968,
		field_12971,
		field_12973,
		field_12970,
		field_12975,
		field_12974,
		field_12969;
	}
}
