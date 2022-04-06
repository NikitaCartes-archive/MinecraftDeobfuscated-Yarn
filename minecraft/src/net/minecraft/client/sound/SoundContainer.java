package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.random.AbstractRandom;

@Environment(EnvType.CLIENT)
public interface SoundContainer<T> {
	int getWeight();

	T getSound(AbstractRandom random);

	void preload(SoundSystem soundSystem);
}
