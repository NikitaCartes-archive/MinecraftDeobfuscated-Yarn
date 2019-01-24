package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ClientCommandServerPacket implements Packet<ServerPlayPacketListener> {
	private int entityId;
	private ClientCommandServerPacket.Mode mode;
	private int mountJumpHeight;

	public ClientCommandServerPacket() {
	}

	@Environment(EnvType.CLIENT)
	public ClientCommandServerPacket(Entity entity, ClientCommandServerPacket.Mode mode) {
		this(entity, mode, 0);
	}

	@Environment(EnvType.CLIENT)
	public ClientCommandServerPacket(Entity entity, ClientCommandServerPacket.Mode mode, int i) {
		this.entityId = entity.getEntityId();
		this.mode = mode;
		this.mountJumpHeight = i;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.entityId = packetByteBuf.readVarInt();
		this.mode = packetByteBuf.readEnumConstant(ClientCommandServerPacket.Mode.class);
		this.mountJumpHeight = packetByteBuf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.entityId);
		packetByteBuf.writeEnumConstant(this.mode);
		packetByteBuf.writeVarInt(this.mountJumpHeight);
	}

	public void method_12364(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onClientCommand(this);
	}

	public ClientCommandServerPacket.Mode getMode() {
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
