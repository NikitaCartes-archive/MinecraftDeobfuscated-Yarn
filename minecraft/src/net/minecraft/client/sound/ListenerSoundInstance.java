package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ListenerSoundInstance {
	void method_4884(SoundInstance soundInstance, WeightedSoundSet weightedSoundSet);
}
