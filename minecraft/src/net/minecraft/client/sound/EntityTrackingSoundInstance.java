package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

@Environment(EnvType.CLIENT)
public class EntityTrackingSoundInstance extends MovingSoundInstance {
	private final Entity entity;

	public EntityTrackingSoundInstance(SoundEvent soundEvent, SoundCategory soundCategory, Entity entity) {
		this(soundEvent, soundCategory, 1.0F, 1.0F, entity);
	}

	public EntityTrackingSoundInstance(SoundEvent soundEvent, SoundCategory soundCategory, float f, float g, Entity entity) {
		super(soundEvent, soundCategory);
		this.volume = f;
		this.pitch = g;
		this.entity = entity;
		this.x = (float)this.entity.getX();
		this.y = (float)this.entity.getY();
		this.z = (float)this.entity.getZ();
	}

	@Override
	public void tick() {
		if (this.entity.removed) {
			this.done = true;
		} else {
			this.x = (float)this.entity.getX();
			this.y = (float)this.entity.getY();
			this.z = (float)this.entity.getZ();
		}
	}
}
