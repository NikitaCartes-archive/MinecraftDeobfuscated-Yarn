package net.minecraft.client.util.telemetry;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class WorldLoadedEvent implements TelemetryEvent {
	private final WorldLoadedEvent.Callback callback;
	private boolean sent;
	@Nullable
	private TelemetryEventProperty.GameMode gameMode = null;
	@Nullable
	private String brand;

	public WorldLoadedEvent(WorldLoadedEvent.Callback callback) {
		this.callback = callback;
	}

	public void putServerType(PropertyMap.Builder builder) {
		if (this.brand != null) {
			builder.put(TelemetryEventProperty.SERVER_MODDED, !this.brand.equals("vanilla"));
		}

		builder.put(TelemetryEventProperty.SERVER_TYPE, this.getServerType());
	}

	private TelemetryEventProperty.ServerType getServerType() {
		if (MinecraftClient.getInstance().isConnectedToRealms()) {
			return TelemetryEventProperty.ServerType.REALM;
		} else {
			return MinecraftClient.getInstance().isIntegratedServerRunning() ? TelemetryEventProperty.ServerType.LOCAL : TelemetryEventProperty.ServerType.OTHER;
		}
	}

	@Override
	public void send(TelemetrySender sender) {
		if (!this.sent && this.gameMode != null) {
			this.sent = true;
			this.callback.onWorldLoadSent();
			sender.send(TelemetryEventType.WORLD_LOADED, builder -> builder.put(TelemetryEventProperty.GAME_MODE, this.gameMode));
		}
	}

	public void setGameMode(GameMode gameMode, boolean hardcore) {
		this.gameMode = switch (gameMode) {
			case SURVIVAL -> hardcore ? TelemetryEventProperty.GameMode.HARDCORE : TelemetryEventProperty.GameMode.SURVIVAL;
			case CREATIVE -> TelemetryEventProperty.GameMode.CREATIVE;
			case ADVENTURE -> TelemetryEventProperty.GameMode.ADVENTURE;
			case SPECTATOR -> TelemetryEventProperty.GameMode.SPECTATOR;
		};
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	@Nullable
	public String getBrand() {
		return this.brand;
	}

	@Environment(EnvType.CLIENT)
	public interface Callback {
		void onWorldLoadSent();
	}
}
