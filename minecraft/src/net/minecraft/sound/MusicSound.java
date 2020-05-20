package net.minecraft.sound;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class MusicSound {
	public static final Codec<MusicSound> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					SoundEvent.field_24628.fieldOf("sound").forGetter(musicSound -> musicSound.event),
					Codec.INT.fieldOf("min_delay").forGetter(musicSound -> musicSound.field_24058),
					Codec.INT.fieldOf("max_delay").forGetter(musicSound -> musicSound.field_24059),
					Codec.BOOL.fieldOf("replace_current_music").forGetter(musicSound -> musicSound.field_24060)
				)
				.apply(instance, MusicSound::new)
	);
	private final SoundEvent event;
	private final int field_24058;
	private final int field_24059;
	private final boolean field_24060;

	public MusicSound(SoundEvent event, int i, int j, boolean bl) {
		this.event = event;
		this.field_24058 = i;
		this.field_24059 = j;
		this.field_24060 = bl;
	}

	@Environment(EnvType.CLIENT)
	public SoundEvent getEvent() {
		return this.event;
	}

	@Environment(EnvType.CLIENT)
	public int method_27280() {
		return this.field_24058;
	}

	@Environment(EnvType.CLIENT)
	public int method_27281() {
		return this.field_24059;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_27282() {
		return this.field_24060;
	}
}
