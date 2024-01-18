package net.minecraft.network.packet.c2s.play;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class ClientCommandC2SPacket implements Packet<ServerPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, ClientCommandC2SPacket> CODEC = Packet.createCodec(ClientCommandC2SPacket::write, ClientCommandC2SPacket::new);
	private final int entityId;
	private final ClientCommandC2SPacket.Mode mode;
	private final int mountJumpHeight;

	public ClientCommandC2SPacket(Entity entity, ClientCommandC2SPacket.Mode mode) {
		this(entity, mode, 0);
	}

	public ClientCommandC2SPacket(Entity entity, ClientCommandC2SPacket.Mode mode, int mountJumpHeight) {
		this.entityId = entity.getId();
		this.mode = mode;
		this.mountJumpHeight = mountJumpHeight;
	}

	private ClientCommandC2SPacket(PacketByteBuf buf) {
		this.entityId = buf.readVarInt();
		this.mode = buf.readEnumConstant(ClientCommandC2SPacket.Mode.class);
		this.mountJumpHeight = buf.readVarInt();
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeEnumConstant(this.mode);
		buf.writeVarInt(this.mountJumpHeight);
	}

	@Override
	public PacketType<ClientCommandC2SPacket> getPacketId() {
		return PlayPackets.PLAYER_COMMAND;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onClientCommand(this);
	}

	public int getEntityId() {
		return this.entityId;
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
