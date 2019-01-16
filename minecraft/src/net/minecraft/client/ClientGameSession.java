package net.minecraft.client;

import com.mojang.bridge.game.GameSession;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ScoreboardEntry;
import net.minecraft.client.world.ClientWorld;

@Environment(EnvType.CLIENT)
public class ClientGameSession implements GameSession {
	private final int playerCount;
	private final boolean remoteServer;
	private final String difficulty;
	private final String gameMode;
	private final UUID sessionId;

	public ClientGameSession(ClientWorld clientWorld, ClientPlayerEntity clientPlayerEntity, ClientPlayNetworkHandler clientPlayNetworkHandler) {
		this.playerCount = clientPlayNetworkHandler.method_2880().size();
		this.remoteServer = !clientPlayNetworkHandler.getClientConnection().isLocal();
		this.difficulty = clientWorld.getDifficulty().getTranslationKey();
		ScoreboardEntry scoreboardEntry = clientPlayNetworkHandler.method_2871(clientPlayerEntity.getUuid());
		if (scoreboardEntry != null) {
			this.gameMode = scoreboardEntry.getGameMode().getName();
		} else {
			this.gameMode = "unknown";
		}

		this.sessionId = clientPlayNetworkHandler.method_16690();
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
