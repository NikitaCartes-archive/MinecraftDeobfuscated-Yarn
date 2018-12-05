package net.minecraft.client.audio;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SoundEntry {
	private final List<Sound> sounds;
	private final boolean replaceable;
	private final String subtitle;

	public SoundEntry(List<Sound> list, boolean bl, String string) {
		this.sounds = list;
		this.replaceable = bl;
		this.subtitle = string;
	}

	public List<Sound> getSounds() {
		return this.sounds;
	}

	public boolean isReplaceable() {
		return this.replaceable;
	}

	@Nullable
	public String getSubtitle() {
		return this.subtitle;
	}
}
