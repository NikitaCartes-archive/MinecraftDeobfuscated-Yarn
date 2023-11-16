package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.session.report.log.ChatLog;
import net.minecraft.client.session.report.log.ChatLogEntry;
import net.minecraft.client.session.report.log.ReceivedMessage;

@Environment(EnvType.CLIENT)
public class SocialInteractionsPlayerListWidget extends ElementListWidget<SocialInteractionsPlayerListEntry> {
	private final SocialInteractionsScreen parent;
	private final List<SocialInteractionsPlayerListEntry> players = Lists.<SocialInteractionsPlayerListEntry>newArrayList();
	@Nullable
	private String currentSearch;

	public SocialInteractionsPlayerListWidget(SocialInteractionsScreen parent, MinecraftClient client, int width, int height, int y, int itemHeight) {
		super(client, width, height, y, itemHeight);
		this.parent = parent;
		this.setRenderBackground(false);
	}

	@Override
	protected void enableScissor(DrawContext context) {
		context.enableScissor(this.getX(), this.getY() + 4, this.getRight(), this.getBottom());
	}

	public void update(Collection<UUID> uuids, double scrollAmount, boolean includeOffline) {
		Map<UUID, SocialInteractionsPlayerListEntry> map = new HashMap();
		this.setPlayers(uuids, map);
		this.markOfflineMembers(map, includeOffline);
		this.refresh(map.values(), scrollAmount);
	}

	private void setPlayers(Collection<UUID> playerUuids, Map<UUID, SocialInteractionsPlayerListEntry> entriesByUuids) {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;

		for (UUID uUID : playerUuids) {
			PlayerListEntry playerListEntry = clientPlayNetworkHandler.getPlayerListEntry(uUID);
			if (playerListEntry != null) {
				boolean bl = playerListEntry.hasPublicKey();
				entriesByUuids.put(
					uUID, new SocialInteractionsPlayerListEntry(this.client, this.parent, uUID, playerListEntry.getProfile().getName(), playerListEntry::getSkinTextures, bl)
				);
			}
		}
	}

	private void markOfflineMembers(Map<UUID, SocialInteractionsPlayerListEntry> entries, boolean includeOffline) {
		for (GameProfile gameProfile : collectReportableProfiles(this.client.getAbuseReportContext().getChatLog())) {
			SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry;
			if (includeOffline) {
				socialInteractionsPlayerListEntry = (SocialInteractionsPlayerListEntry)entries.computeIfAbsent(
					gameProfile.getId(),
					uuid -> {
						SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntryx = new SocialInteractionsPlayerListEntry(
							this.client, this.parent, gameProfile.getId(), gameProfile.getName(), this.client.getSkinProvider().getSkinTexturesSupplier(gameProfile), true
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

	private static Collection<GameProfile> collectReportableProfiles(ChatLog log) {
		Set<GameProfile> set = new ObjectLinkedOpenHashSet<>();

		for (int i = log.getMaxIndex(); i >= log.getMinIndex(); i--) {
			ChatLogEntry chatLogEntry = log.get(i);
			if (chatLogEntry instanceof ReceivedMessage.ChatMessage) {
				ReceivedMessage.ChatMessage chatMessage = (ReceivedMessage.ChatMessage)chatLogEntry;
				if (chatMessage.message().hasSignature()) {
					set.add(chatMessage.profile());
				}
			}
		}

		return set;
	}

	private void sortPlayers() {
		this.players.sort(Comparator.comparing(player -> {
			if (this.client.uuidEquals(player.getUuid())) {
				return 0;
			} else if (this.client.getAbuseReportContext().draftPlayerUuidEquals(player.getUuid())) {
				return 1;
			} else if (player.getUuid().version() == 2) {
				return 4;
			} else {
				return player.hasSentMessage() ? 2 : 3;
			}
		}).thenComparing(player -> {
			if (!player.getName().isBlank()) {
				int i = player.getName().codePointAt(0);
				if (i == 95 || i >= 97 && i <= 122 || i >= 65 && i <= 90 || i >= 48 && i <= 57) {
					return 0;
				}
			}

			return 1;
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
			boolean bl = player.hasPublicKey();
			SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntryx = new SocialInteractionsPlayerListEntry(
				this.client, this.parent, player.getProfile().getId(), player.getProfile().getName(), player::getSkinTextures, bl
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
