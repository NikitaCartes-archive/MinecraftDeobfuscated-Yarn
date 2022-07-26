package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.base.Strings;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
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
		this.setRenderBackground(false);
		this.setRenderHorizontalShadows(false);
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

	public void update(Collection<UUID> uuids, double scrollAmount, boolean includeOffline) {
		Map<UUID, SocialInteractionsPlayerListEntry> map = new HashMap();
		this.setPlayers(uuids, map);
		this.markOfflineMembers(map, includeOffline);
		this.refresh(map.values(), scrollAmount);
	}

	private void setPlayers(Collection<UUID> playerUuids, Map<UUID, SocialInteractionsPlayerListEntry> map) {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;

		for (UUID uUID : playerUuids) {
			PlayerListEntry playerListEntry = clientPlayNetworkHandler.getPlayerListEntry(uUID);
			if (playerListEntry != null) {
				UUID uUID2 = playerListEntry.getProfile().getId();
				boolean bl = playerListEntry.getPublicKeyData() != null;
				map.put(
					uUID2, new SocialInteractionsPlayerListEntry(this.client, this.parent, uUID2, playerListEntry.getProfile().getName(), playerListEntry::getSkinTexture, bl)
				);
			}
		}
	}

	private void markOfflineMembers(Map<UUID, SocialInteractionsPlayerListEntry> entries, boolean includeOffline) {
		for (GameProfile gameProfile : this.client.getAbuseReportContext().chatLog().streamBackward().collectSenderProfiles()) {
			SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry;
			if (includeOffline) {
				socialInteractionsPlayerListEntry = (SocialInteractionsPlayerListEntry)entries.computeIfAbsent(
					gameProfile.getId(),
					uuid -> {
						SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntryx = new SocialInteractionsPlayerListEntry(
							this.client, this.parent, gameProfile.getId(), gameProfile.getName(), Suppliers.memoize(() -> this.client.getSkinProvider().loadSkin(gameProfile)), true
						);
						socialInteractionsPlayerListEntryx.setOffline(true);
						return socialInteractionsPlayerListEntryx;
					}
				);
			} else {
				socialInteractionsPlayerListEntry = (SocialInteractionsPlayerListEntry)entries.get(gameProfile.getId());
				if (socialInteractionsPlayerListEntry == null) {
					continue;
				}
			}

			socialInteractionsPlayerListEntry.setSentMessage(true);
		}
	}

	private void sortPlayers() {
		this.players.sort(Comparator.comparing(player -> {
			if (player.getUuid().equals(this.client.getSession().getUuidOrNull())) {
				return 0;
			} else if (player.getUuid().version() == 2) {
				return 3;
			} else {
				return player.hasSentMessage() ? 1 : 2;
			}
		}).thenComparing(player -> {
			int i = player.getName().codePointAt(0);
			return i != 95 && (i < 97 || i > 122) && (i < 65 || i > 90) && (i < 48 || i > 57) ? 1 : 0;
		}).thenComparing(SocialInteractionsPlayerListEntry::getName, String::compareToIgnoreCase));
	}

	private void refresh(Collection<SocialInteractionsPlayerListEntry> players, double scrollAmount) {
		this.players.clear();
		this.players.addAll(players);
		this.sortPlayers();
		this.filterPlayers();
		this.replaceEntries(this.players);
		this.setScrollAmount(scrollAmount);
	}

	private void filterPlayers() {
		if (this.currentSearch != null) {
			this.players.removeIf(player -> !player.getName().toLowerCase(Locale.ROOT).contains(this.currentSearch));
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
			boolean bl = player.getPublicKeyData() != null;
			SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntryx = new SocialInteractionsPlayerListEntry(
				this.client, this.parent, player.getProfile().getId(), player.getProfile().getName(), player::getSkinTexture, bl
			);
			this.addEntry(socialInteractionsPlayerListEntryx);
			this.players.add(socialInteractionsPlayerListEntryx);
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
