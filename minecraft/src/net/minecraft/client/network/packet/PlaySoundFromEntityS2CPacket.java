package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.Validate;

public class PlaySoundFromEntityS2CPacket implements Packet<ClientPlayPacketListener> {
	private SoundEvent sound;
	private SoundCategory category;
	private int entityId;
	private float volume;
	private float pitch;

	public PlaySoundFromEntityS2CPacket() {
	}

	public PlaySoundFromEntityS2CPacket(SoundEvent soundEvent, SoundCategory soundCategory, Entity entity, float f, float g) {
		Validate.notNull(soundEvent, "sound");
		this.sound = soundEvent;
		this.category = soundCategory;
		this.entityId = entity.getEntityId();
		this.volume = f;
		this.pitch = g;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.sound = Registry.SOUND_EVENT.getInt(packetByteBuf.readVarInt());
		this.category = packetByteBuf.readEnumConstant(SoundCategory.class);
		this.entityId = packetByteBuf.readVarInt();
		this.volume = packetByteBuf.readFloat();
		this.pitch = packetByteBuf.readFloat();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(Registry.SOUND_EVENT.getRawId(this.sound));
		packetByteBuf.writeEnumConstant(this.category);
		packetByteBuf.writeVarInt(this.entityId);
		packetByteBuf.writeFloat(this.volume);
		packetByteBuf.writeFloat(this.pitch);
	}

	@Environment(EnvType.CLIENT)
	public SoundEvent getSound() {
		return this.sound;
	}

	@Environment(EnvType.CLIENT)
	public SoundCategory getCategory() {
		return this.category;
	}

	@Environment(EnvType.CLIENT)
	public int getEntityId() {
		return this.entityId;
	}

	@Environment(EnvType.CLIENT)
	public float getVolume() {
		return this.volume;
	}

	@Environment(EnvType.CLIENT)
	public float getPitch() {
		return this.pitch;
	}

	public void method_11884(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11125(this);
	}
}
