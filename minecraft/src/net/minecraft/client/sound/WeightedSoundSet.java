package net.minecraft.client.sound;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.AbstractRandom;

@Environment(EnvType.CLIENT)
public class WeightedSoundSet implements SoundContainer<Sound> {
	private final List<SoundContainer<Sound>> sounds = Lists.<SoundContainer<Sound>>newArrayList();
	private final AbstractRandom random = AbstractRandom.createAtomic();
	private final Identifier id;
	@Nullable
	private final Text subtitle;

	public WeightedSoundSet(Identifier id, @Nullable String subtitle) {
		this.id = id;
		this.subtitle = subtitle == null ? null : Text.translatable(subtitle);
	}

	@Override
	public int getWeight() {
		int i = 0;

		for (SoundContainer<Sound> soundContainer : this.sounds) {
			i += soundContainer.getWeight();
		}

		return i;
	}

	public Sound getSound(AbstractRandom abstractRandom) {
		int i = this.getWeight();
		if (!this.sounds.isEmpty() && i != 0) {
			int j = abstractRandom.nextInt(i);

			for (SoundContainer<Sound> soundContainer : this.sounds) {
				j -= soundContainer.getWeight();
				if (j < 0) {
					return soundContainer.getSound(abstractRandom);
				}
			}

			return SoundManager.MISSING_SOUND;
		} else {
			return SoundManager.MISSING_SOUND;
		}
	}

	public void add(SoundContainer<Sound> container) {
		this.sounds.add(container);
	}

	public Identifier getId() {
		return this.id;
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
