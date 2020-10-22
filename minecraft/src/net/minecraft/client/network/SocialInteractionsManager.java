package net.minecraft.client.network;

import com.google.common.collect.Sets;
import com.mojang.authlib.minecraft.SocialInteractionsService;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;

@Environment(EnvType.CLIENT)
public class SocialInteractionsManager {
	private final MinecraftClient client;
	private final Set<UUID> hiddenPlayers = Sets.<UUID>newHashSet();
	private final SocialInteractionsService field_26912;

	public SocialInteractionsManager(MinecraftClient client, SocialInteractionsService socialInteractionsService) {
		this.client = client;
		this.field_26912 = socialInteractionsService;
	}

	public void hidePlayer(UUID uuid) {
		this.hiddenPlayers.add(uuid);
	}

	public void showPlayer(UUID uuid) {
		this.hiddenPlayers.remove(uuid);
	}

	public boolean method_31391(UUID uUID) {
		return this.isPlayerHidden(uUID) || this.method_31392(uUID);
	}

	public boolean isPlayerHidden(UUID uuid) {
		return this.hiddenPlayers.contains(uuid);
	}

	public boolean method_31392(UUID uUID) {
		return this.field_26912.isBlockedPlayer(uUID);
	}

	public Set<UUID> getHiddenPlayers() {
		return this.hiddenPlayers;
	}

	public void method_31337(PlayerListEntry playerListEntry) {
		Screen screen = this.client.currentScreen;
		if (screen instanceof SocialInteractionsScreen) {
			SocialInteractionsScreen socialInteractionsScreen = (SocialInteractionsScreen)screen;
			socialInteractionsScreen.method_31353(playerListEntry);
		}
	}

	public void method_31341(UUID uUID) {
		Screen screen = this.client.currentScreen;
		if (screen instanceof SocialInteractionsScreen) {
			SocialInteractionsScreen socialInteractionsScreen = (SocialInteractionsScreen)screen;
			socialInteractionsScreen.method_31355(uUID);
		}
	}
}
