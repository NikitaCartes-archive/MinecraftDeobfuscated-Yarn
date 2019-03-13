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
	private SoundEvent field_12642;
	private SoundCategory field_12641;
	private int entityId;
	private float volume;
	private float pitch;

	public PlaySoundFromEntityS2CPacket() {
	}

	public PlaySoundFromEntityS2CPacket(SoundEvent soundEvent, SoundCategory soundCategory, Entity entity, float f, float g) {
		Validate.notNull(soundEvent, "sound");
		this.field_12642 = soundEvent;
		this.field_12641 = soundCategory;
		this.entityId = entity.getEntityId();
		this.volume = f;
		this.pitch = g;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12642 = Registry.SOUND_EVENT.get(packetByteBuf.readVarInt());
		this.field_12641 = packetByteBuf.readEnumConstant(SoundCategory.class);
		this.entityId = packetByteBuf.readVarInt();
		this.volume = packetByteBuf.readFloat();
		this.pitch = packetByteBuf.readFloat();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(Registry.SOUND_EVENT.getRawId(this.field_12642));
		packetByteBuf.writeEnumConstant(this.field_12641);
		packetByteBuf.writeVarInt(this.entityId);
		packetByteBuf.writeFloat(this.volume);
		packetByteBuf.writeFloat(this.pitch);
	}

	@Environment(EnvType.CLIENT)
	public SoundEvent method_11882() {
		return this.field_12642;
	}

	@Environment(EnvType.CLIENT)
	public SoundCategory method_11881() {
		return this.field_12641;
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
