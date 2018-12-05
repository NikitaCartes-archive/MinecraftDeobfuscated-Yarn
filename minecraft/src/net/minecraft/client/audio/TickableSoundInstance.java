package net.minecraft.client.audio;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Tickable;

@Environment(EnvType.CLIENT)
public interface TickableSoundInstance extends SoundInstance, Tickable {
	boolean isDone();
}
