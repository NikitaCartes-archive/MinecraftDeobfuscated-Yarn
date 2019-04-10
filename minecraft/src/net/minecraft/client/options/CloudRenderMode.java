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

	private static final CloudRenderMode[] RENDER_MODES = (CloudRenderMode[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(CloudRenderMode::getRenderModeId))
		.toArray(CloudRenderMode[]::new);
	private final int renderModeId;
	private final String renderMode;

	private CloudRenderMode(int j, String string2) {
		this.renderModeId = j;
		this.renderMode = string2;
	}

	public int getRenderModeId() {
		return this.renderModeId;
	}

	public String getRenderMode() {
		return this.renderMode;
	}

	public static CloudRenderMode fromId(int i) {
		return RENDER_MODES[MathHelper.floorMod(i, RENDER_MODES.length)];
	}
}
