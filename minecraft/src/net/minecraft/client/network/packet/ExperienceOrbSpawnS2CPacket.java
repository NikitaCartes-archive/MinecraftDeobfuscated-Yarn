package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ExperienceOrbSpawnS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private double x;
	private double y;
	private double z;
	private int experience;

	public ExperienceOrbSpawnS2CPacket() {
	}

	public ExperienceOrbSpawnS2CPacket(ExperienceOrbEntity experienceOrbEntity) {
		this.id = experienceOrbEntity.getEntityId();
		this.x = experienceOrbEntity.getX();
		this.y = experienceOrbEntity.getY();
		this.z = experienceOrbEntity.getZ();
		this.experience = experienceOrbEntity.getExperienceAmount();
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readVarInt();
		this.x = packetByteBuf.readDouble();
		this.y = packetByteBuf.readDouble();
		this.z = packetByteBuf.readDouble();
		this.experience = packetByteBuf.readShort();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.id);
		packetByteBuf.writeDouble(this.x);
		packetByteBuf.writeDouble(this.y);
		packetByteBuf.writeDouble(this.z);
		packetByteBuf.writeShort(this.experience);
	}

	public void method_11182(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onExperienceOrbSpawn(this);
	}

	@Environment(EnvType.CLIENT)
	public int getId() {
		return this.id;
	}

	@Environment(EnvType.CLIENT)
	public double getX() {
		return this.x;
	}

	@Environment(EnvType.CLIENT)
	public double getY() {
		return this.y;
	}

	@Environment(EnvType.CLIENT)
	public double getZ() {
		return this.z;
	}

	@Environment(EnvType.CLIENT)
	public int getExperience() {
		return this.experience;
	}
}
