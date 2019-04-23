package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface TickableSoundInstance extends SoundInstance {
	boolean isDone();

	void tick();
}
