package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.World;

public class EntitySetHeadYawClientPacket implements Packet<ClientPlayPacketListener> {
	private int entity;
	private byte headYaw;

	public EntitySetHeadYawClientPacket() {
	}

	public EntitySetHeadYawClientPacket(Entity entity, byte b) {
		this.entity = entity.getEntityId();
		this.headYaw = b;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.entity = packetByteBuf.readVarInt();
		this.headYaw = packetByteBuf.readByte();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.entity);
		packetByteBuf.writeByte(this.headYaw);
	}

	public void method_11788(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntitySetHeadYaw(this);
	}

	@Environment(EnvType.CLIENT)
	public Entity getEntity(World world) {
		return world.getEntityById(this.entity);
	}

	@Environment(EnvType.CLIENT)
	public byte getHeadYaw() {
		return this.headYaw;
	}
}
