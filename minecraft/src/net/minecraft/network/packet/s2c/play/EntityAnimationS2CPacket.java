package net.minecraft.network.packet.s2c.play;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class EntityAnimationS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, EntityAnimationS2CPacket> CODEC = Packet.createCodec(
		EntityAnimationS2CPacket::write, EntityAnimationS2CPacket::new
	);
	public static final int SWING_MAIN_HAND = 0;
	public static final int WAKE_UP = 2;
	public static final int SWING_OFF_HAND = 3;
	public static final int CRIT = 4;
	public static final int ENCHANTED_HIT = 5;
	private final int entityId;
	private final int animationId;

	public EntityAnimationS2CPacket(Entity entity, int animationId) {
		this.entityId = entity.getId();
		this.animationId = animationId;
	}

	private EntityAnimationS2CPacket(PacketByteBuf buf) {
		this.entityId = buf.readVarInt();
		this.animationId = buf.readUnsignedByte();
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeByte(this.animationId);
	}

	@Override
	public PacketType<EntityAnimationS2CPacket> getPacketId() {
		return PlayPackets.ANIMATE;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityAnimation(this);
	}

	public int getEntityId() {
		return this.entityId;
	}

	public int getAnimationId() {
		return this.animationId;
	}
}
