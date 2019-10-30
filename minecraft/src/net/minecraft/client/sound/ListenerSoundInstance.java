package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ListenerSoundInstance {
	void onSoundPlayed(SoundInstance sound, WeightedSoundSet soundSet);
}
