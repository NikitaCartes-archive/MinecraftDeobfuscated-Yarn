package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

@Environment(EnvType.CLIENT)
public class EntityTrackingSoundInstance extends MovingSoundInstance {
	private final Entity entity;

	public EntityTrackingSoundInstance(SoundEvent sound, SoundCategory soundCategory, Entity entity) {
		this(sound, soundCategory, 1.0F, 1.0F, entity);
	}

	public EntityTrackingSoundInstance(SoundEvent sound, SoundCategory soundCategory, float volume, float pitch, Entity entity) {
		super(sound, soundCategory);
		this.volume = volume;
		this.pitch = pitch;
		this.entity = entity;
		this.x = (float)this.entity.getX();
		this.y = (float)this.entity.getY();
		this.z = (float)this.entity.getZ();
	}

	@Override
	public boolean canPlay() {
		return !this.entity.isSilent();
	}

	@Override
	public void tick() {
		if (this.entity.removed) {
			this.setDone();
		} else {
			this.x = (float)this.entity.getX();
			this.y = (float)this.entity.getY();
			this.z = (float)this.entity.getZ();
		}
	}
}
