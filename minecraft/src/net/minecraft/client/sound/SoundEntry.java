package net.minecraft.client.sound;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SoundEntry {
	private final List<Sound> sounds;
	private final boolean replace;
	private final String subtitle;

	public SoundEntry(List<Sound> sounds, boolean replace, String subtitle) {
		this.sounds = sounds;
		this.replace = replace;
		this.subtitle = subtitle;
	}

	public List<Sound> getSounds() {
		return this.sounds;
	}

	public boolean canReplace() {
		return this.replace;
	}

	@Nullable
	public String getSubtitle() {
		return this.subtitle;
	}
}
