package net.minecraft.client.sound;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WeightedSoundSet implements SoundContainer<Sound> {
	private final List<SoundContainer<Sound>> sounds = Lists.<SoundContainer<Sound>>newArrayList();
	private final Random random = new Random();
	private final Identifier id;
	private final Text subtitle;

	public WeightedSoundSet(Identifier identifier, @Nullable String string) {
		this.id = identifier;
		this.subtitle = string == null ? null : new TranslatableText(string);
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

			return SoundManager.MISSING_SOUND;
		} else {
			return SoundManager.MISSING_SOUND;
		}
	}

	public void add(SoundContainer<Sound> soundContainer) {
		this.sounds.add(soundContainer);
	}

	@Nullable
	public Text getSubtitle() {
		return this.subtitle;
	}

	@Override
	public void preload(SoundSystem soundSystem) {
		for (SoundContainer<Sound> soundContainer : this.sounds) {
			soundContainer.preload(soundSystem);
		}
	}
}
