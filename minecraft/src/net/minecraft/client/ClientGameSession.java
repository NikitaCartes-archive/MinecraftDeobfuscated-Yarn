package net.minecraft.client;

import com.mojang.bridge.game.GameSession;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;

@Environment(EnvType.CLIENT)
public class ClientGameSession implements GameSession {
	private final int playerCount;
	private final boolean remoteServer;
	private final String difficulty;
	private final String gameMode;
	private final UUID sessionId;

	public ClientGameSession(ClientWorld world, ClientPlayerEntity player, ClientPlayNetworkHandler networkHandler) {
		this.playerCount = networkHandler.getPlayerList().size();
		this.remoteServer = !networkHandler.getConnection().isLocal();
		this.difficulty = world.getDifficulty().getName();
		PlayerListEntry playerListEntry = networkHandler.getPlayerListEntry(player.getUuid());
		if (playerListEntry != null) {
			this.gameMode = playerListEntry.getGameMode().getName();
		} else {
			this.gameMode = "unknown";
		}

		this.sessionId = networkHandler.getSessionId();
	}

	@Override
	public int getPlayerCount() {
		return this.playerCount;
	}

	@Override
	public boolean isRemoteServer() {
		return this.remoteServer;
	}

	@Override
	public String getDifficulty() {
		return this.difficulty;
	}

	@Override
	public String getGameMode() {
		return this.gameMode;
	}

	@Override
	public UUID getSessionId() {
		return this.sessionId;
	}
}
