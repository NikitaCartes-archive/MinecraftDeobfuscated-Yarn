package net.minecraft.sound;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

public class SoundEvent {
	public static final Codec<SoundEvent> CODEC = Identifier.CODEC.xmap(SoundEvent::new, soundEvent -> soundEvent.id);
	private final Identifier id;

	public SoundEvent(Identifier id) {
		this.id = id;
	}

	@Environment(EnvType.CLIENT)
	public Identifier getId() {
		return this.id;
	}
}
