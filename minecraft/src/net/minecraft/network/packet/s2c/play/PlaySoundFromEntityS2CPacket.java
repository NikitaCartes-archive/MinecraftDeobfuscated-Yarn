package net.minecraft.network.packet.s2c.play;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class PlaySoundFromEntityS2CPacket implements Packet<ClientPlayPacketListener> {
	private final RegistryEntry<SoundEvent> sound;
	private final SoundCategory category;
	private final int entityId;
	private final float volume;
	private final float pitch;
	private final long seed;

	public PlaySoundFromEntityS2CPacket(RegistryEntry<SoundEvent> sound, SoundCategory category, Entity entity, float volume, float pitch, long seed) {
		this.sound = sound;
		this.category = category;
		this.entityId = entity.getId();
		this.volume = volume;
		this.pitch = pitch;
		this.seed = seed;
	}

	public PlaySoundFromEntityS2CPacket(PacketByteBuf buf) {
		this.sound = buf.readRegistryEntry(Registries.SOUND_EVENT.getIndexedEntries(), SoundEvent::fromBuf);
		this.category = buf.readEnumConstant(SoundCategory.class);
		this.entityId = buf.readVarInt();
		this.volume = buf.readFloat();
		this.pitch = buf.readFloat();
		this.seed = buf.readLong();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeRegistryEntry(Registries.SOUND_EVENT.getIndexedEntries(), this.sound, (packetByteBuf, soundEvent) -> soundEvent.writeBuf(packetByteBuf));
		buf.writeEnumConstant(this.category);
		buf.writeVarInt(this.entityId);
		buf.writeFloat(this.volume);
		buf.writeFloat(this.pitch);
		buf.writeLong(this.seed);
	}

	public RegistryEntry<SoundEvent> getSound() {
		return this.sound;
	}

	public SoundCategory getCategory() {
		return this.category;
	}

	public int getEntityId() {
		return this.entityId;
	}

	public float getVolume() {
		return this.volume;
	}

	public float getPitch() {
		return this.pitch;
	}

	public long getSeed() {
		return this.seed;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlaySoundFromEntity(this);
	}
}
