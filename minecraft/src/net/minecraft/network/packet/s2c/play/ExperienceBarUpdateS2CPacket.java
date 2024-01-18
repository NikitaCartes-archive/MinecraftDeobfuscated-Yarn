package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class ExperienceBarUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, ExperienceBarUpdateS2CPacket> CODEC = Packet.createCodec(
		ExperienceBarUpdateS2CPacket::write, ExperienceBarUpdateS2CPacket::new
	);
	private final float barProgress;
	private final int experienceLevel;
	private final int experience;

	public ExperienceBarUpdateS2CPacket(float barProgress, int experienceLevel, int experience) {
		this.barProgress = barProgress;
		this.experienceLevel = experienceLevel;
		this.experience = experience;
	}

	private ExperienceBarUpdateS2CPacket(PacketByteBuf buf) {
		this.barProgress = buf.readFloat();
		this.experience = buf.readVarInt();
		this.experienceLevel = buf.readVarInt();
	}

	private void write(PacketByteBuf buf) {
		buf.writeFloat(this.barProgress);
		buf.writeVarInt(this.experience);
		buf.writeVarInt(this.experienceLevel);
	}

	@Override
	public PacketType<ExperienceBarUpdateS2CPacket> getPacketId() {
		return PlayPackets.SET_EXPERIENCE;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onExperienceBarUpdate(this);
	}

	public float getBarProgress() {
		return this.barProgress;
	}

	public int getExperienceLevel() {
		return this.experienceLevel;
	}

	public int getExperience() {
		return this.experience;
	}
}
