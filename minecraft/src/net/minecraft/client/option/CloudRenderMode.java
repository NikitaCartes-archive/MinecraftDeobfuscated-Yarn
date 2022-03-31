package net.minecraft.client.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.TranslatableOption;

@Environment(EnvType.CLIENT)
public enum CloudRenderMode implements TranslatableOption {
	OFF(0, "options.off"),
	FAST(1, "options.clouds.fast"),
	FANCY(2, "options.clouds.fancy");

	private final int id;
	private final String translationKey;

	private CloudRenderMode(int id, String translationKey) {
		this.id = id;
		this.translationKey = translationKey;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public String getTranslationKey() {
		return this.translationKey;
	}
}
