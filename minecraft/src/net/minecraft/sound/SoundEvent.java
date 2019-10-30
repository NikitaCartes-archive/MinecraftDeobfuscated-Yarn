package net.minecraft.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

public class SoundEvent {
	private final Identifier id;

	public SoundEvent(Identifier id) {
		this.id = id;
	}

	@Environment(EnvType.CLIENT)
	public Identifier getId() {
		return this.id;
	}
}
