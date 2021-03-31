package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.world.World;

public class SetCameraEntityS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int entityId;

	public SetCameraEntityS2CPacket(Entity entity) {
		this.entityId = entity.getId();
	}

	public SetCameraEntityS2CPacket(PacketByteBuf buf) {
		this.entityId = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.entityId);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSetCameraEntity(this);
	}

	@Nullable
	public Entity getEntity(World world) {
		return world.getEntityById(this.entityId);
	}
}
