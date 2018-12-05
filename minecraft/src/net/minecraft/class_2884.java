package net.minecraft;

import java.io.IOException;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.PacketByteBuf;

public class class_2884 implements Packet<ServerPlayPacketListener> {
	private UUID field_13129;

	public class_2884() {
	}

	public class_2884(UUID uUID) {
		this.field_13129 = uUID;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_13129 = packetByteBuf.readUuid();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeUuid(this.field_13129);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12073(this);
	}

	@Nullable
	public Entity method_12541(ServerWorld serverWorld) {
		return serverWorld.getEntityByUuid(this.field_13129);
	}
}
