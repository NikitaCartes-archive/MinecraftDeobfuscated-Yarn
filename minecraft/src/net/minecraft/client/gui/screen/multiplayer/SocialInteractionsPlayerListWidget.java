package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class SocialInteractionsPlayerListWidget extends ElementListWidget<SocialInteractionsPlayerListEntry> {
	private final SocialInteractionsScreen parent;
	private final List<SocialInteractionsPlayerListEntry> players = Lists.<SocialInteractionsPlayerListEntry>newArrayList();
	@Nullable
	private String currentSearch;

	public SocialInteractionsPlayerListWidget(SocialInteractionsScreen parent, MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
		super(client, width, height, top, bottom, itemHeight);
		this.parent = parent;
		this.method_31322(false);
		this.method_31323(false);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		double d = this.client.getWindow().getScaleFactor();
		RenderSystem.enableScissor(
			(int)((double)this.getRowLeft() * d),
			(int)((double)(this.height - this.bottom) * d),
			(int)((double)(this.getScrollbarPositionX() + 6) * d),
			(int)((double)(this.height - (this.height - this.bottom) - this.top - 4) * d)
		);
		super.render(matrices, mouseX, mouseY, delta);
		RenderSystem.disableScissor();
	}

	public void update(Collection<UUID> uuids, double scrollAmount) {
		this.players.clear();

		for (UUID uUID : uuids) {
			PlayerListEntry playerListEntry = this.client.player.networkHandler.getPlayerListEntry(uUID);
			if (playerListEntry != null) {
				this.players
					.add(
						new SocialInteractionsPlayerListEntry(
							this.client, this.parent, playerListEntry.getProfile().getId(), playerListEntry.getProfile().getName(), playerListEntry::getSkinTexture
						)
					);
			}
		}

		this.filterPlayers();
		this.players
			.sort(
				(socialInteractionsPlayerListEntry, socialInteractionsPlayerListEntry2) -> socialInteractionsPlayerListEntry.getName()
						.compareToIgnoreCase(socialInteractionsPlayerListEntry2.getName())
			);
		this.replaceEntries(this.players);
		this.setScrollAmount(scrollAmount);
	}

	private void filterPlayers() {
		if (this.currentSearch != null) {
			this.players
				.removeIf(socialInteractionsPlayerListEntry -> !socialInteractionsPlayerListEntry.getName().toLowerCase(Locale.ROOT).contains(this.currentSearch));
			this.replaceEntries(this.players);
		}
	}

	public void setCurrentSearch(String currentSearch) {
		this.currentSearch = currentSearch;
	}

	public boolean isEmpty() {
		return this.players.isEmpty();
	}

	public void setPlayerOnline(PlayerListEntry player, SocialInteractionsScreen.Tab tab) {
		UUID uUID = player.getProfile().getId();

		for (SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry : this.players) {
			if (socialInteractionsPlayerListEntry.getUuid().equals(uUID)) {
				socialInteractionsPlayerListEntry.setOffline(false);
				return;
			}
		}

		if ((tab == SocialInteractionsScreen.Tab.ALL || this.client.getSocialInteractionsManager().isPlayerMuted(uUID))
			&& (Strings.isNullOrEmpty(this.currentSearch) || player.getProfile().getName().toLowerCase(Locale.ROOT).contains(this.currentSearch))) {
			SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry2 = new SocialInteractionsPlayerListEntry(
				this.client, this.parent, player.getProfile().getId(), player.getProfile().getName(), player::getSkinTexture
			);
			this.addEntry(socialInteractionsPlayerListEntry2);
			this.players.add(socialInteractionsPlayerListEntry2);
		}
	}

	public void setPlayerOffline(UUID uuid) {
		for (SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry : this.players) {
			if (socialInteractionsPlayerListEntry.getUuid().equals(uuid)) {
				socialInteractionsPlayerListEntry.setOffline(true);
				return;
			}
		}
	}
}
