package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public class ExperienceBarUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private final float barProgress;
	private final int experienceLevel;
	private final int experience;

	public ExperienceBarUpdateS2CPacket(float barProgress, int experienceLevel, int experience) {
		this.barProgress = barProgress;
		this.experienceLevel = experienceLevel;
		this.experience = experience;
	}

	public ExperienceBarUpdateS2CPacket(PacketByteBuf buf) {
		this.barProgress = buf.readFloat();
		this.experience = buf.readVarInt();
		this.experienceLevel = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeFloat(this.barProgress);
		buf.writeVarInt(this.experience);
		buf.writeVarInt(this.experienceLevel);
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
