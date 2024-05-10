package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class ProjectilePowerS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, ProjectilePowerS2CPacket> CODEC = Packet.createCodec(
		ProjectilePowerS2CPacket::write, ProjectilePowerS2CPacket::new
	);
	private final int entityId;
	private final double field_51849;

	public ProjectilePowerS2CPacket(int entityId, double powerX) {
		this.entityId = entityId;
		this.field_51849 = powerX;
	}

	private ProjectilePowerS2CPacket(PacketByteBuf buf) {
		this.entityId = buf.readVarInt();
		this.field_51849 = buf.readDouble();
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeDouble(this.field_51849);
	}

	@Override
	public PacketType<ProjectilePowerS2CPacket> getPacketId() {
		return PlayPackets.PROJECTILE_POWER;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onProjectilePower(this);
	}

	public int getEntityId() {
		return this.entityId;
	}

	public double method_60423() {
		return this.field_51849;
	}
}
