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
	public ClientCommandC2SPacket(Entity entity, ClientCommandC2SPacket.Mode mode, int i) {
		this.entityId = entity.getEntityId();
		this.mode = mode;
		this.mountJumpHeight = i;
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

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onClientCommand(this);
	}

	public ClientCommandC2SPacket.Mode getMode() {
		return this.mode;
	}

	public int getMountJumpHeight() {
		return this.mountJumpHeight;
	}

	public static enum Mode {
		PRESS_SHIFT_KEY,
		RELEASE_SHIFT_KEY,
		STOP_SLEEPING,
		START_SPRINTING,
		STOP_SPRINTING,
		START_RIDING_JUMP,
		STOP_RIDING_JUMP,
		OPEN_INVENTORY,
		START_FALL_FLYING;
	}
}
