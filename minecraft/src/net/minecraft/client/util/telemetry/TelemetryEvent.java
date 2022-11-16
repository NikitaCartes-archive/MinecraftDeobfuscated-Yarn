package net.minecraft.client.util.telemetry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface TelemetryEvent {
	void send(TelemetrySender sender);
}
