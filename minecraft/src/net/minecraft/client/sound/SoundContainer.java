package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface SoundContainer<T> {
	int getWeight();

	T getSound();

	void preload(SoundSystem soundSystem);
}
