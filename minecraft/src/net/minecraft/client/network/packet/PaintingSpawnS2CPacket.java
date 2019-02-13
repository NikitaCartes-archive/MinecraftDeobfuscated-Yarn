package net.minecraft.client.network.packet;

import java.io.IOException;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public class PaintingSpawnS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private UUID uuid;
	private BlockPos pos;
	private Direction facing;
	private int motive;

	public PaintingSpawnS2CPacket() {
	}

	public PaintingSpawnS2CPacket(PaintingEntity paintingEntity) {
		this.id = paintingEntity.getEntityId();
		this.uuid = paintingEntity.getUuid();
		this.pos = paintingEntity.getDecorationBlockPos();
		this.facing = paintingEntity.facing;
		this.motive = Registry.MOTIVE.getRawId(paintingEntity.motive);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readVarInt();
		this.uuid = packetByteBuf.readUuid();
		this.motive = packetByteBuf.readVarInt();
		this.pos = packetByteBuf.readBlockPos();
		this.facing = Direction.fromHorizontal(packetByteBuf.readUnsignedByte());
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.id);
		packetByteBuf.writeUuid(this.uuid);
		packetByteBuf.writeVarInt(this.motive);
		packetByteBuf.writeBlockPos(this.pos);
		packetByteBuf.writeByte(this.facing.getHorizontal());
	}

	public void method_11224(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPaintingSpawn(this);
	}

	@Environment(EnvType.CLIENT)
	public int getId() {
		return this.id;
	}

	@Environment(EnvType.CLIENT)
	public UUID getPaintingUuid() {
		return this.uuid;
	}

	@Environment(EnvType.CLIENT)
	public BlockPos getPos() {
		return this.pos;
	}

	@Environment(EnvType.CLIENT)
	public Direction getFacing() {
		return this.facing;
	}

	@Environment(EnvType.CLIENT)
	public PaintingMotive getMotive() {
		return Registry.MOTIVE.get(this.motive);
	}
}
