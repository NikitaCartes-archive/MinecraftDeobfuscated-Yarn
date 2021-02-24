package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.Validate;

public class PlaySoundFromEntityS2CPacket implements Packet<ClientPlayPacketListener> {
	private final SoundEvent sound;
	private final SoundCategory category;
	private final int entityId;
	private final float volume;
	private final float pitch;

	public PlaySoundFromEntityS2CPacket(SoundEvent sound, SoundCategory category, Entity entity, float volume, float pitch) {
		Validate.notNull(sound, "sound");
		this.sound = sound;
		this.category = category;
		this.entityId = entity.getId();
		this.volume = volume;
		this.pitch = pitch;
	}

	public PlaySoundFromEntityS2CPacket(PacketByteBuf packetByteBuf) {
		this.sound = Registry.SOUND_EVENT.get(packetByteBuf.readVarInt());
		this.category = packetByteBuf.readEnumConstant(SoundCategory.class);
		this.entityId = packetByteBuf.readVarInt();
		this.volume = packetByteBuf.readFloat();
		this.pitch = packetByteBuf.readFloat();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(Registry.SOUND_EVENT.getRawId(this.sound));
		buf.writeEnumConstant(this.category);
		buf.writeVarInt(this.entityId);
		buf.writeFloat(this.volume);
		buf.writeFloat(this.pitch);
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

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlaySoundFromEntity(this);
	}
}
