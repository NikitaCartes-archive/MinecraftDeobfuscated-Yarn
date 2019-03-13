package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WeightedSoundSet implements SoundContainer<Sound> {
	private final List<SoundContainer<Sound>> sounds = Lists.<SoundContainer<Sound>>newArrayList();
	private final Random random = new Random();
	private final Identifier field_5602;
	private final TextComponent field_5599;

	public WeightedSoundSet(Identifier identifier, @Nullable String string) {
		this.field_5602 = identifier;
		this.field_5599 = string == null ? null : new TranslatableTextComponent(string);
	}

	@Override
	public int getWeight() {
		int i = 0;

		for (SoundContainer<Sound> soundContainer : this.sounds) {
			i += soundContainer.getWeight();
		}

		return i;
	}

	public Sound method_4887() {
		int i = this.getWeight();
		if (!this.sounds.isEmpty() && i != 0) {
			int j = this.random.nextInt(i);

			for (SoundContainer<Sound> soundContainer : this.sounds) {
				j -= soundContainer.getWeight();
				if (j < 0) {
					return soundContainer.getSound();
				}
			}

			return SoundLoader.SOUND_MISSING;
		} else {
			return SoundLoader.SOUND_MISSING;
		}
	}

	public void method_4885(SoundContainer<Sound> soundContainer) {
		this.sounds.add(soundContainer);
	}

	@Nullable
	public TextComponent method_4886() {
		return this.field_5599;
	}

	@Override
	public void method_18188(SoundManager soundManager) {
		for (SoundContainer<Sound> soundContainer : this.sounds) {
			soundContainer.method_18188(soundManager);
		}
	}
}
