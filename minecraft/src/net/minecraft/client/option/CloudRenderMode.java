package net.minecraft.client.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum CloudRenderMode {
	OFF("options.off"),
	FAST("options.clouds.fast"),
	FANCY("options.clouds.fancy");

	private final String translationKey;

	private CloudRenderMode(String translationKey) {
		this.translationKey = translationKey;
	}

	public String getTranslationKey() {
		return this.translationKey;
	}
}
