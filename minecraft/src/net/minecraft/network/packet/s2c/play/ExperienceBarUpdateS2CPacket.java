package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class ExperienceBarUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private float barProgress;
	private int experienceLevel;
	private int experience;

	public ExperienceBarUpdateS2CPacket() {
	}

	public ExperienceBarUpdateS2CPacket(float barProgress, int experienceLevel, int experience) {
		this.barProgress = barProgress;
		this.experienceLevel = experienceLevel;
		this.experience = experience;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.barProgress = buf.readFloat();
		this.experience = buf.readVarInt();
		this.experienceLevel = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
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
