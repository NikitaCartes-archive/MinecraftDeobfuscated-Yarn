package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class ExperienceBarUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private final float barProgress;
	private final int experienceLevel;
	private final int experience;

	public ExperienceBarUpdateS2CPacket(float barProgress, int experienceLevel, int experience) {
		this.barProgress = barProgress;
		this.experienceLevel = experienceLevel;
		this.experience = experience;
	}

	public ExperienceBarUpdateS2CPacket(PacketByteBuf packetByteBuf) {
		this.barProgress = packetByteBuf.readFloat();
		this.experience = packetByteBuf.readVarInt();
		this.experienceLevel = packetByteBuf.readVarInt();
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

	@Environment(EnvType.CLIENT)
	public float getBarProgress() {
		return this.barProgress;
	}

	@Environment(EnvType.CLIENT)
	public int getExperienceLevel() {
		return this.experienceLevel;
	}

	@Environment(EnvType.CLIENT)
	public int getExperience() {
		return this.experience;
	}
}
