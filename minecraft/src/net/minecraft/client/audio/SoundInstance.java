package net.minecraft.client.audio;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface SoundInstance {
	Identifier method_4775();

	@Nullable
	WeightedSoundSet method_4783(SoundLoader soundLoader);

	Sound getSound();

	SoundCategory method_4774();

	boolean isRepeatable();

	boolean method_4787();

	int getRepeatDelay();

	float getVolume();

	float getPitch();

	float getX();

	float getY();

	float getZ();

	SoundInstance.AttenuationType getAttenuationType();

	default boolean method_4785() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	public static enum AttenuationType {
		NONE(0),
		LINEAR(2);

		private final int TYPE;

		private AttenuationType(int j) {
			this.TYPE = j;
		}

		public int getType() {
			return this.TYPE;
		}
	}
}
