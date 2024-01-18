package net.minecraft.network.packet.s2c.play;

import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class ExperienceOrbSpawnS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, ExperienceOrbSpawnS2CPacket> CODEC = Packet.createCodec(
		ExperienceOrbSpawnS2CPacket::write, ExperienceOrbSpawnS2CPacket::new
	);
	private final int id;
	private final double x;
	private final double y;
	private final double z;
	private final int experience;

	public ExperienceOrbSpawnS2CPacket(ExperienceOrbEntity experienceOrbEntity) {
		this.id = experienceOrbEntity.getId();
		this.x = experienceOrbEntity.getX();
		this.y = experienceOrbEntity.getY();
		this.z = experienceOrbEntity.getZ();
		this.experience = experienceOrbEntity.getExperienceAmount();
	}

	private ExperienceOrbSpawnS2CPacket(PacketByteBuf buf) {
		this.id = buf.readVarInt();
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.experience = buf.readShort();
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.id);
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeShort(this.experience);
	}

	@Override
	public PacketType<ExperienceOrbSpawnS2CPacket> getPacketId() {
		return PlayPackets.ADD_EXPERIENCE_ORB;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onExperienceOrbSpawn(this);
	}

	public int getId() {
		return this.id;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getZ() {
		return this.z;
	}

	public int getExperience() {
		return this.experience;
	}
}
