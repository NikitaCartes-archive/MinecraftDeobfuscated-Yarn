package net.minecraft.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.options.GameOption;

@Environment(EnvType.CLIENT)
public final class WindowProvider implements AutoCloseable {
	private final MinecraftClient client;
	private final MonitorTracker monitorTracker;

	public WindowProvider(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
		this.monitorTracker = new MonitorTracker(this::createMonitor);
	}

	public Monitor createMonitor(long l) {
		Monitor monitor = new Monitor(this.monitorTracker, l);
		GameOption.FULLSCREEN_RESOLUTION.setMax((float)monitor.getVideoModeCount());
		return monitor;
	}

	public Window createWindow(WindowSettings windowSettings, String string, String string2) {
		return new Window(this.client, this.monitorTracker, windowSettings, string, string2);
	}

	public void close() {
		this.monitorTracker.stop();
	}
}
