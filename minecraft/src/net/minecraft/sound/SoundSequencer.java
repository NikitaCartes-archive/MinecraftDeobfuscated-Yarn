package net.minecraft.sound;

@FunctionalInterface
public interface SoundSequencer {
	void waitThenPlay(int delay, SoundEvent sound, SoundCategory category, float volume, float pitch);
}
