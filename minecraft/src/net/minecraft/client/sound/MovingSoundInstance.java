package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.AbstractRandom;

@Environment(EnvType.CLIENT)
public abstract class MovingSoundInstance extends AbstractSoundInstance implements TickableSoundInstance {
	private boolean done;

	protected MovingSoundInstance(SoundEvent soundEvent, SoundCategory soundCategory, AbstractRandom abstractRandom) {
		super(soundEvent, soundCategory, abstractRandom);
	}

	@Override
	public boolean isDone() {
		return this.done;
	}

	protected final void setDone() {
		this.done = true;
		this.repeat = false;
	}
}
