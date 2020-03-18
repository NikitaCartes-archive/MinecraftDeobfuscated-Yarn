package net.minecraft.client.sound;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface SoundInstance {
	Identifier getId();

	@Nullable
	WeightedSoundSet getSoundSet(SoundManager soundManager);

	Sound getSound();

	SoundCategory getCategory();

	boolean isRepeatable();

	boolean isLooping();

	int getRepeatDelay();

	float getVolume();

	float getPitch();

	float getX();

	float getY();

	float getZ();

	SoundInstance.AttenuationType getAttenuationType();

	default boolean shouldAlwaysPlay() {
		return false;
	}

	default boolean canPlay() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	public static enum AttenuationType {
		NONE,
		LINEAR;
	}
}
