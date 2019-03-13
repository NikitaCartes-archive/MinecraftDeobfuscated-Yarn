package net.minecraft.client.network.packet;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.World;

public class SetCameraEntityS2CPacket implements Packet<ClientPlayPacketListener> {
	public int id;

	public SetCameraEntityS2CPacket() {
	}

	public SetCameraEntityS2CPacket(Entity entity) {
		this.id = entity.getEntityId();
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.id);
	}

	public void method_11801(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11111(this);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Entity getEntity(World world) {
		return world.getEntityById(this.id);
	}
}
