package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

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
	public ClientCommandC2SPacket(Entity entity, ClientCommandC2SPacket.Mode mode, int i) {
		this.entityId = entity.getEntityId();
		this.mode = mode;
		this.mountJumpHeight = i;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.entityId = packetByteBuf.readVarInt();
		this.mode = packetByteBuf.readEnumConstant(ClientCommandC2SPacket.Mode.class);
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

	public ClientCommandC2SPacket.Mode getMode() {
		return this.mode;
	}

	public int getMountJumpHeight() {
		return this.mountJumpHeight;
	}

	public static enum Mode {
		START_SNEAKING,
		STOP_SNEAKING,
		STOP_SLEEPING,
		START_SPRINTING,
		STOP_SPRINTING,
		START_RIDING_JUMP,
		STOP_RIDING_JUMP,
		OPEN_INVENTORY,
		START_FALL_FLYING;
	}
}
