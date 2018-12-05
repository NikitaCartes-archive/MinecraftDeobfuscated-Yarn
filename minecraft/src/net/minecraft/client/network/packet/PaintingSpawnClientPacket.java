package net.minecraft.client.network.packet;

import java.io.IOException;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.decoration.PaintingEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.sortme.PaintingMotive;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public class PaintingSpawnClientPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private UUID uuid;
	private BlockPos pos;
	private Direction facing;
	private int field_12010;

	public PaintingSpawnClientPacket() {
	}

	public PaintingSpawnClientPacket(PaintingEntity paintingEntity) {
		this.id = paintingEntity.getEntityId();
		this.uuid = paintingEntity.getUuid();
		this.pos = paintingEntity.getDecorationBlockPos();
		this.facing = paintingEntity.field_7099;
		this.field_12010 = Registry.MOTIVE.getRawId(paintingEntity.type);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readVarInt();
		this.uuid = packetByteBuf.readUuid();
		this.field_12010 = packetByteBuf.readVarInt();
		this.pos = packetByteBuf.readBlockPos();
		this.facing = Direction.fromHorizontal(packetByteBuf.readUnsignedByte());
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.id);
		packetByteBuf.writeUuid(this.uuid);
		packetByteBuf.writeVarInt(this.field_12010);
		packetByteBuf.writeBlockPos(this.pos);
		packetByteBuf.writeByte(this.facing.getHorizontal());
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
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
	public PaintingMotive method_11221() {
		return Registry.MOTIVE.getInt(this.field_12010);
	}
}
