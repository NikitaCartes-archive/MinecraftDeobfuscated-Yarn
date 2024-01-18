package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.world.World;

public class SetCameraEntityS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, SetCameraEntityS2CPacket> CODEC = Packet.createCodec(
		SetCameraEntityS2CPacket::write, SetCameraEntityS2CPacket::new
	);
	private final int entityId;

	public SetCameraEntityS2CPacket(Entity entity) {
		this.entityId = entity.getId();
	}

	private SetCameraEntityS2CPacket(PacketByteBuf buf) {
		this.entityId = buf.readVarInt();
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.entityId);
	}

	@Override
	public PacketType<SetCameraEntityS2CPacket> getPacketId() {
		return PlayPackets.SET_CAMERA;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSetCameraEntity(this);
	}

	@Nullable
	public Entity getEntity(World world) {
		return world.getEntityById(this.entityId);
	}
}
