package net.minecraft.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

public class SoundEvent {
	private final Identifier id;

	public SoundEvent(Identifier identifier) {
		this.id = identifier;
	}

	@Environment(EnvType.CLIENT)
	public Identifier getId() {
		return this.id;
	}
}
