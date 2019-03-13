package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.World;

public class EntityStatusS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private byte status;

	public EntityStatusS2CPacket() {
	}

	public EntityStatusS2CPacket(Entity entity, byte b) {
		this.id = entity.getEntityId();
		this.status = b;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readInt();
		this.status = packetByteBuf.readByte();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeInt(this.id);
		packetByteBuf.writeByte(this.status);
	}

	public void method_11471(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11148(this);
	}

	@Environment(EnvType.CLIENT)
	public Entity getEntity(World world) {
		return world.getEntityById(this.id);
	}

	@Environment(EnvType.CLIENT)
	public byte getStatus() {
		return this.status;
	}
}
