package net.minecraft.client.session.telemetry;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class WorldLoadedEvent {
	private boolean sent;
	@Nullable
	private TelemetryEventProperty.GameMode gameMode;
	@Nullable
	private String brand;
	@Nullable
	private final String minigameName;

	public WorldLoadedEvent(@Nullable String minigameName) {
		this.minigameName = minigameName;
	}

	public void putServerType(PropertyMap.Builder builder) {
		if (this.brand != null) {
			builder.put(TelemetryEventProperty.SERVER_MODDED, !this.brand.equals("vanilla"));
		}

		builder.put(TelemetryEventProperty.SERVER_TYPE, this.getServerType());
	}

	private TelemetryEventProperty.ServerType getServerType() {
		ServerInfo serverInfo = MinecraftClient.getInstance().getCurrentServerEntry();
		if (serverInfo != null && serverInfo.isRealm()) {
			return TelemetryEventProperty.ServerType.REALM;
		} else {
			return MinecraftClient.getInstance().isIntegratedServerRunning() ? TelemetryEventProperty.ServerType.LOCAL : TelemetryEventProperty.ServerType.OTHER;
		}
	}

	public boolean send(TelemetrySender sender) {
		if (!this.sent && this.gameMode != null && this.brand != null) {
			this.sent = true;
			sender.send(TelemetryEventType.WORLD_LOADED, adder -> {
				adder.put(TelemetryEventProperty.GAME_MODE, this.gameMode);
				if (this.minigameName != null) {
					adder.put(TelemetryEventProperty.REALMS_MAP_CONTENT, this.minigameName);
				}
			});
			return true;
		} else {
			return false;
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
}
