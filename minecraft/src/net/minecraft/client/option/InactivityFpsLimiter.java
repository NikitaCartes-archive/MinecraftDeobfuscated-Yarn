package net.minecraft.client.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class InactivityFpsLimiter {
	private static final int IN_GUI_FPS = 60;
	private static final int MINIMIZED_FPS = 10;
	private static final int AFK_STAGE_1_FPS = 30;
	private static final int AFK_STAGE_2_FPS = 10;
	private static final long AFK_STAGE_1_THRESHOLD = 60000L;
	private static final long AFK_STAGE_2_THRESHOLD = 600000L;
	private final GameOptions options;
	private final MinecraftClient client;
	private int maxFps;
	private long lastInputTime;

	public InactivityFpsLimiter(GameOptions options, MinecraftClient client) {
		this.options = options;
		this.client = client;
		this.maxFps = options.getMaxFps().getValue();
	}

	public int update() {
		InactivityFpsLimit inactivityFpsLimit = this.options.getInactivityFpsLimit().getValue();
		if (this.client.getWindow().isMinimized()) {
			return 10;
		} else {
			if (inactivityFpsLimit == InactivityFpsLimit.AFK) {
				long l = Util.getMeasuringTimeMs() - this.lastInputTime;
				if (l > 600000L) {
					return 10;
				}

				if (l > 60000L) {
					return Math.min(this.maxFps, 30);
				}
			}

			return this.client.world != null || this.client.currentScreen == null && this.client.getOverlay() == null ? this.maxFps : 60;
		}
	}

	public void setMaxFps(int maxFps) {
		this.maxFps = maxFps;
	}

	public void onInput() {
		this.lastInputTime = Util.getMeasuringTimeMs();
	}
}
