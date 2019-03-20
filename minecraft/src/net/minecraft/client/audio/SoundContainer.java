package net.minecraft.client.audio;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface SoundContainer<T> {
	int getWeight();

	T getSound();

	void addTo(SoundSystem soundSystem);
}
