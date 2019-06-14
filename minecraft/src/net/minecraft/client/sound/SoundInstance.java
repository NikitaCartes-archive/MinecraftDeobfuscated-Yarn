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
	WeightedSoundSet method_4783(SoundManager soundManager);

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

	@Environment(EnvType.CLIENT)
	public static enum AttenuationType {
		field_5478,
		field_5476;
	}
}
