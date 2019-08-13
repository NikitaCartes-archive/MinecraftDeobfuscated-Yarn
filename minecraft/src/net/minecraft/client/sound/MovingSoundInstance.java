package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

@Environment(EnvType.CLIENT)
public abstract class MovingSoundInstance extends AbstractSoundInstance implements TickableSoundInstance {
	protected boolean done;

	protected MovingSoundInstance(SoundEvent soundEvent, SoundCategory soundCategory) {
		super(soundEvent, soundCategory);
	}

	@Override
	public boolean isDone() {
		return this.done;
	}
}
