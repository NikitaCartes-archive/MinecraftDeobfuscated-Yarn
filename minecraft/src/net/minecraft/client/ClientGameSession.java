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

	public ClientGameSession(ClientWorld clientWorld, ClientPlayerEntity clientPlayerEntity, ClientPlayNetworkHandler clientPlayNetworkHandler) {
		this.playerCount = clientPlayNetworkHandler.getPlayerList().size();
		this.remoteServer = !clientPlayNetworkHandler.getClientConnection().isLocal();
		this.difficulty = clientWorld.getDifficulty().getName();
		PlayerListEntry playerListEntry = clientPlayNetworkHandler.method_2871(clientPlayerEntity.getUuid());
		if (playerListEntry != null) {
			this.gameMode = playerListEntry.getGameMode().getName();
		} else {
			this.gameMode = "unknown";
		}

		this.sessionId = clientPlayNetworkHandler.getSessionId();
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
