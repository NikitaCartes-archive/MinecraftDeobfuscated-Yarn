package net.minecraft.client.network;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.SocialInteractionsService;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class SocialInteractionsManager {
	private final MinecraftClient client;
	private final Set<UUID> hiddenPlayers = Sets.<UUID>newHashSet();
	private final SocialInteractionsService socialInteractionsService;
	private final Map<String, UUID> playerNameByUuid = Maps.<String, UUID>newHashMap();

	public SocialInteractionsManager(MinecraftClient client, SocialInteractionsService socialInteractionsService) {
		this.client = client;
		this.socialInteractionsService = socialInteractionsService;
	}

	public void hidePlayer(UUID uuid) {
		this.hiddenPlayers.add(uuid);
	}

	public void showPlayer(UUID uuid) {
		this.hiddenPlayers.remove(uuid);
	}

	public boolean isPlayerMuted(UUID uuid) {
		return this.isPlayerHidden(uuid) || this.isPlayerBlocked(uuid);
	}

	public boolean isPlayerHidden(UUID uuid) {
		return this.hiddenPlayers.contains(uuid);
	}

	public boolean isPlayerBlocked(UUID uuid) {
		return this.socialInteractionsService.isBlockedPlayer(uuid);
	}

	public Set<UUID> getHiddenPlayers() {
		return this.hiddenPlayers;
	}

	public UUID getUuid(String playerName) {
		return (UUID)this.playerNameByUuid.getOrDefault(playerName, Util.NIL_UUID);
	}

	public void setPlayerOnline(PlayerListEntry player) {
		GameProfile gameProfile = player.getProfile();
		if (gameProfile.isComplete()) {
			this.playerNameByUuid.put(gameProfile.getName(), gameProfile.getId());
		}

		if (this.client.currentScreen instanceof SocialInteractionsScreen socialInteractionsScreen) {
			socialInteractionsScreen.setPlayerOnline(player);
		}
	}

	public void setPlayerOffline(UUID uuid) {
		if (this.client.currentScreen instanceof SocialInteractionsScreen socialInteractionsScreen) {
			socialInteractionsScreen.setPlayerOffline(uuid);
		}
	}
}
