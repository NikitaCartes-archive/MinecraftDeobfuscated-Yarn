package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketIdentifier;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.world.World;

public class EntityStatusS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, EntityStatusS2CPacket> CODEC = Packet.createCodec(EntityStatusS2CPacket::write, EntityStatusS2CPacket::new);
	private final int id;
	private final byte status;

	public EntityStatusS2CPacket(Entity entity, byte status) {
		this.id = entity.getId();
		this.status = status;
	}

	private EntityStatusS2CPacket(PacketByteBuf buf) {
		this.id = buf.readInt();
		this.status = buf.readByte();
	}

	private void write(PacketByteBuf buf) {
		buf.writeInt(this.id);
		buf.writeByte(this.status);
	}

	@Override
	public PacketIdentifier<EntityStatusS2CPacket> getPacketId() {
		return PlayPackets.ENTITY_EVENT;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityStatus(this);
	}

	@Nullable
	public Entity getEntity(World world) {
		return world.getEntityById(this.id);
	}

	public byte getStatus() {
		return this.status;
	}
}
