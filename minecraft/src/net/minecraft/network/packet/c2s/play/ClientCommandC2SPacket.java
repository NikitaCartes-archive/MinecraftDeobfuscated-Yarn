package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class ClientCommandC2SPacket implements Packet<ServerPlayPacketListener> {
	private int entityId;
	private ClientCommandC2SPacket.Mode mode;
	private int mountJumpHeight;

	public ClientCommandC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public ClientCommandC2SPacket(Entity entity, ClientCommandC2SPacket.Mode mode) {
		this(entity, mode, 0);
	}

	@Environment(EnvType.CLIENT)
	public ClientCommandC2SPacket(Entity entity, ClientCommandC2SPacket.Mode mode, int mountJumpHeight) {
		this.entityId = entity.getEntityId();
		this.mode = mode;
		this.mountJumpHeight = mountJumpHeight;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.entityId = buf.readVarInt();
		this.mode = buf.readEnumConstant(ClientCommandC2SPacket.Mode.class);
		this.mountJumpHeight = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.entityId);
		buf.writeEnumConstant(this.mode);
		buf.writeVarInt(this.mountJumpHeight);
	}

	public void method_12364(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onClientCommand(this);
	}

	public ClientCommandC2SPacket.Mode getMode() {
		return this.mode;
	}

	public int getMountJumpHeight() {
		return this.mountJumpHeight;
	}

	public static enum Mode {
		field_12979,
		field_12984,
		field_12986,
		field_12981,
		field_12985,
		field_12987,
		field_12980,
		field_12988,
		field_12982;
	}
}
