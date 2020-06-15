package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.world.World;

public class SetCameraEntityS2CPacket implements Packet<ClientPlayPacketListener> {
	public int entityId;

	public SetCameraEntityS2CPacket() {
	}

	public SetCameraEntityS2CPacket(Entity entity) {
		this.entityId = entity.getEntityId();
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.entityId = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.entityId);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSetCameraEntity(this);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Entity getEntity(World world) {
		return world.getEntityById(this.entityId);
	}
}
