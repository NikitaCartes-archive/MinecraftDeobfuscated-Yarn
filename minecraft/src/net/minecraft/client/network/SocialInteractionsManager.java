package net.minecraft.client.network;

import com.google.common.collect.Sets;
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

	public SocialInteractionsManager(MinecraftClient client) {
		this.client = client;
	}

	public void hidePlayer(UUID uuid) {
		this.hiddenPlayers.add(uuid);
	}

	public void showPlayer(UUID uuid) {
		this.hiddenPlayers.remove(uuid);
	}

	public boolean isPlayerHidden(UUID uuid) {
		return this.hiddenPlayers.contains(uuid);
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
