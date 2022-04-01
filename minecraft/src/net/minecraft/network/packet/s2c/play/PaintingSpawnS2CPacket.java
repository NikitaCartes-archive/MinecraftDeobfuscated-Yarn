package net.minecraft.network.packet.s2c.play;

import java.util.UUID;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public class PaintingSpawnS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int id;
	private final UUID uuid;
	private final BlockPos pos;
	private final Direction facing;
	private final int motiveId;

	public PaintingSpawnS2CPacket(PaintingEntity entity) {
		this.id = entity.getId();
		this.uuid = entity.getUuid();
		this.pos = entity.getDecorationBlockPos();
		this.facing = entity.getHorizontalFacing();
		this.motiveId = Registry.PAINTING_MOTIVE.getRawId(entity.motive);
	}

	public PaintingSpawnS2CPacket(PacketByteBuf buf) {
		this.id = buf.readVarInt();
		this.uuid = buf.readUuid();
		this.motiveId = buf.readVarInt();
		this.pos = buf.readBlockPos();
		this.facing = Direction.fromHorizontal(buf.readUnsignedByte());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.id);
		buf.writeUuid(this.uuid);
		buf.writeVarInt(this.motiveId);
		buf.writeBlockPos(this.pos);
		buf.writeByte(this.facing.getHorizontal());
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPaintingSpawn(this);
	}

	public int getId() {
		return this.id;
	}

	public UUID getPaintingUuid() {
		return this.uuid;
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public Direction getFacing() {
		return this.facing;
	}

	public PaintingMotive getMotive() {
		return Registry.PAINTING_MOTIVE.get(this.motiveId);
	}
}
