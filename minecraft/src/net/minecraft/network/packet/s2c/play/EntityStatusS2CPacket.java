package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.world.World;

public class EntityStatusS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int id;
	private final byte status;

	public EntityStatusS2CPacket(Entity entity, byte status) {
		this.id = entity.getId();
		this.status = status;
	}

	public EntityStatusS2CPacket(PacketByteBuf packetByteBuf) {
		this.id = packetByteBuf.readInt();
		this.status = packetByteBuf.readByte();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeInt(this.id);
		buf.writeByte(this.status);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityStatus(this);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Entity getEntity(World world) {
		return world.getEntityById(this.id);
	}

	@Environment(EnvType.CLIENT)
	public byte getStatus() {
		return this.status;
	}
}
