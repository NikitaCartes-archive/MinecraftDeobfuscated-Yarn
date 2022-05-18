package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public abstract class MovingSoundInstance extends AbstractSoundInstance implements TickableSoundInstance {
	private boolean done;

	protected MovingSoundInstance(SoundEvent soundEvent, SoundCategory soundCategory, Random random) {
		super(soundEvent, soundCategory, random);
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
