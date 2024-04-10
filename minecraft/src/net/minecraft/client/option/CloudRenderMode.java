package net.minecraft.client.option;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.TranslatableOption;

@Environment(EnvType.CLIENT)
public enum CloudRenderMode implements TranslatableOption, StringIdentifiable {
	OFF(0, "false", "options.off"),
	FAST(1, "fast", "options.clouds.fast"),
	FANCY(2, "true", "options.clouds.fancy");

	public static final Codec<CloudRenderMode> CODEC = StringIdentifiable.createCodec(CloudRenderMode::values);
	private final int id;
	private final String serializedId;
	private final String translationKey;

	private CloudRenderMode(final int id, final String serializedId, final String translationKey) {
		this.id = id;
		this.serializedId = serializedId;
		this.translationKey = translationKey;
	}

	@Override
	public String asString() {
		return this.serializedId;
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
