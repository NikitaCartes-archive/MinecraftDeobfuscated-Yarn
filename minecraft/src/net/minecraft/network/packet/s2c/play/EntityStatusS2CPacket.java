package net.minecraft.network.packet.s2c.play;

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

	public EntityStatusS2CPacket(Entity entity, byte status) {
		this.id = entity.getEntityId();
		this.status = status;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readInt();
		this.status = buf.readByte();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeInt(this.id);
		buf.writeByte(this.status);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityStatus(this);
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
