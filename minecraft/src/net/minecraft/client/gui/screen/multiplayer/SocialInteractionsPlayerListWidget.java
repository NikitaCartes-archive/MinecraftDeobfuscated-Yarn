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
	private final MinecraftClient minecraftClient;
	private final List<SocialInteractionsPlayerListEntry> players = Lists.<SocialInteractionsPlayerListEntry>newArrayList();
	@Nullable
	private String currentSearch;

	public SocialInteractionsPlayerListWidget(SocialInteractionsScreen parent, MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
		super(client, width, height, top, bottom, itemHeight);
		this.parent = parent;
		this.minecraftClient = client;
		this.method_31322(false);
		this.method_31323(false);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		double d = this.minecraftClient.getWindow().getScaleFactor();
		RenderSystem.enableScissor(
			(int)((double)this.getRowLeft() * d),
			(int)((double)(this.height - this.bottom) * d),
			(int)((double)(this.getScrollbarPositionX() + 6) * d),
			(int)((double)(this.height - (this.height - this.bottom) - this.top - 4) * d)
		);
		super.render(matrices, mouseX, mouseY, delta);
		RenderSystem.disableScissor();
	}

	public void method_31393(Collection<UUID> collection, double d) {
		this.players.clear();

		for (UUID uUID : collection) {
			PlayerListEntry playerListEntry = this.minecraftClient.player.networkHandler.getPlayerListEntry(uUID);
			if (playerListEntry != null) {
				this.players
					.add(
						new SocialInteractionsPlayerListEntry(
							this.minecraftClient, this.parent, playerListEntry.getProfile().getId(), playerListEntry.getProfile().getName(), playerListEntry::getSkinTexture
						)
					);
			}
		}

		this.method_31349();
		this.players
			.sort(
				(socialInteractionsPlayerListEntry, socialInteractionsPlayerListEntry2) -> socialInteractionsPlayerListEntry.getName()
						.compareToIgnoreCase(socialInteractionsPlayerListEntry2.getName())
			);
		this.replaceEntries(this.players);
		this.setScrollAmount(d);
	}

	private void method_31349() {
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

	public void method_31345(PlayerListEntry playerListEntry, SocialInteractionsScreen.Tab tab) {
		UUID uUID = playerListEntry.getProfile().getId();

		for (SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry : this.players) {
			if (socialInteractionsPlayerListEntry.getUuid().equals(uUID)) {
				socialInteractionsPlayerListEntry.method_31335(false);
				return;
			}
		}

		if ((tab == SocialInteractionsScreen.Tab.ALL || this.minecraftClient.getSocialInteractionsManager().method_31391(uUID))
			&& (Strings.isNullOrEmpty(this.currentSearch) || playerListEntry.getProfile().getName().toLowerCase(Locale.ROOT).contains(this.currentSearch))) {
			SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry2 = new SocialInteractionsPlayerListEntry(
				this.minecraftClient, this.parent, playerListEntry.getProfile().getId(), playerListEntry.getProfile().getName(), playerListEntry::getSkinTexture
			);
			this.addEntry(socialInteractionsPlayerListEntry2);
			this.players.add(socialInteractionsPlayerListEntry2);
		}
	}

	public void method_31347(UUID uUID) {
		for (SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry : this.players) {
			if (socialInteractionsPlayerListEntry.getUuid().equals(uUID)) {
				socialInteractionsPlayerListEntry.method_31335(true);
				return;
			}
		}
	}
}
