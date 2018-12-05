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
	private BlockPos field_12967;
	private Direction field_12965;
	private PlayerActionServerPacket.class_2847 field_12966;

	public PlayerActionServerPacket() {
	}

	@Environment(EnvType.CLIENT)
	public PlayerActionServerPacket(PlayerActionServerPacket.class_2847 arg, BlockPos blockPos, Direction direction) {
		this.field_12966 = arg;
		this.field_12967 = blockPos;
		this.field_12965 = direction;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12966 = packetByteBuf.readEnumConstant(PlayerActionServerPacket.class_2847.class);
		this.field_12967 = packetByteBuf.readBlockPos();
		this.field_12965 = Direction.byId(packetByteBuf.readUnsignedByte());
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.field_12966);
		packetByteBuf.writeBlockPos(this.field_12967);
		packetByteBuf.writeByte(this.field_12965.getId());
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPlayerAction(this);
	}

	public BlockPos getPos() {
		return this.field_12967;
	}

	public Direction method_12360() {
		return this.field_12965;
	}

	public PlayerActionServerPacket.class_2847 getAction() {
		return this.field_12966;
	}

	public static enum class_2847 {
		field_12968,
		field_12971,
		field_12973,
		field_12970,
		field_12975,
		field_12974,
		field_12969;
	}
}
