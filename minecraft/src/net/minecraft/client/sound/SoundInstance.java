package net.minecraft.client.sound;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.AbstractRandom;

@Environment(EnvType.CLIENT)
public interface SoundInstance {
	Identifier getId();

	@Nullable
	WeightedSoundSet getSoundSet(SoundManager soundManager);

	Sound getSound();

	SoundCategory getCategory();

	boolean isRepeatable();

	boolean isRelative();

	int getRepeatDelay();

	float getVolume();

	float getPitch();

	double getX();

	double getY();

	double getZ();

	SoundInstance.AttenuationType getAttenuationType();

	default boolean shouldAlwaysPlay() {
		return false;
	}

	default boolean canPlay() {
		return true;
	}

	static AbstractRandom createRandom() {
		return AbstractRandom.createAtomic();
	}

	@Environment(EnvType.CLIENT)
	public static enum AttenuationType {
		NONE,
		LINEAR;
	}
}
