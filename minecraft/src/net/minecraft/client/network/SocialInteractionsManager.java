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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class SocialInteractionsManager {
	private final MinecraftClient client;
	private final Set<UUID> hiddenPlayers = Sets.<UUID>newHashSet();
	private final SocialInteractionsService socialInteractionsService;
	private final Map<String, UUID> field_26927 = Maps.<String, UUID>newHashMap();

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

	public boolean method_31391(UUID uUID) {
		return this.isPlayerHidden(uUID) || this.isPlayerBlocked(uUID);
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

	public UUID method_31407(String string) {
		return (UUID)this.field_26927.getOrDefault(string, Util.NIL_UUID);
	}

	public void method_31337(PlayerListEntry playerListEntry) {
		GameProfile gameProfile = playerListEntry.getProfile();
		if (gameProfile.isComplete()) {
			this.field_26927.put(gameProfile.getName(), gameProfile.getId());
		}

		Screen screen = this.client.currentScreen;
		if (screen instanceof SocialInteractionsScreen) {
			SocialInteractionsScreen socialInteractionsScreen = (SocialInteractionsScreen)screen;
			socialInteractionsScreen.setPlayerOnline(playerListEntry);
		}
	}

	public void method_31341(UUID uUID) {
		Screen screen = this.client.currentScreen;
		if (screen instanceof SocialInteractionsScreen) {
			SocialInteractionsScreen socialInteractionsScreen = (SocialInteractionsScreen)screen;
			socialInteractionsScreen.setPlayerOffline(uUID);
		}
	}
}
