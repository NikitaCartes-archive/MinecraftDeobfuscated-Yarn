package net.minecraft.client.options;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public enum CloudRenderMode {
	field_18162(0, "options.off"),
	field_18163(1, "options.clouds.fast"),
	field_18164(2, "options.clouds.fancy");

	private static final CloudRenderMode[] field_18165 = (CloudRenderMode[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(CloudRenderMode::method_18496))
		.toArray(CloudRenderMode[]::new);
	private final int field_18166;
	private final String field_18167;

	private CloudRenderMode(int j, String string2) {
		this.field_18166 = j;
		this.field_18167 = string2;
	}

	public int method_18496() {
		return this.field_18166;
	}

	public String method_18498() {
		return this.field_18167;
	}

	public static CloudRenderMode method_18497(int i) {
		return field_18165[MathHelper.floorMod(i, field_18165.length)];
	}
}
