package net.minecraft.client.option;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public enum CloudRenderMode {
	OFF(0, "options.off"),
	FAST(1, "options.clouds.fast"),
	FANCY(2, "options.clouds.fancy");

	private static final CloudRenderMode[] VALUES = (CloudRenderMode[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(CloudRenderMode::getId))
		.toArray(CloudRenderMode[]::new);
	private final int id;
	private final String translationKey;

	private CloudRenderMode(int id, String translationKey) {
		this.id = id;
		this.translationKey = translationKey;
	}

	public int getId() {
		return this.id;
	}

	public String getTranslationKey() {
		return this.translationKey;
	}

	public static CloudRenderMode byId(int id) {
		return VALUES[MathHelper.floorMod(id, VALUES.length)];
	}
}
