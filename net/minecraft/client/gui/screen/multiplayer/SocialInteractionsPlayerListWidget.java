/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsPlayerListEntry;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SocialInteractionsPlayerListWidget
extends ElementListWidget<SocialInteractionsPlayerListEntry> {
    private final SocialInteractionsScreen parent;
    private final List<SocialInteractionsPlayerListEntry> players = Lists.newArrayList();
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
        RenderSystem.enableScissor((int)((double)this.getRowLeft() * d), (int)((double)(this.height - this.bottom) * d), (int)((double)(this.getScrollbarPositionX() + 6) * d), (int)((double)(this.height - (this.height - this.bottom) - this.top - 4) * d));
        super.render(matrices, mouseX, mouseY, delta);
        RenderSystem.disableScissor();
    }

    public void update(Collection<UUID> uuids, double scrollAmount, boolean includeOffline) {
        HashMap<UUID, SocialInteractionsPlayerListEntry> map = new HashMap<UUID, SocialInteractionsPlayerListEntry>();
        this.setPlayers(uuids, map);
        this.markOfflineMembers(map, includeOffline);
        this.refresh(map.values(), scrollAmount);
    }

    private void setPlayers(Collection<UUID> playerUuids, Map<UUID, SocialInteractionsPlayerListEntry> map) {
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
        for (UUID uUID : playerUuids) {
            PlayerListEntry playerListEntry = clientPlayNetworkHandler.getPlayerListEntry(uUID);
            if (playerListEntry == null) continue;
            UUID uUID2 = playerListEntry.getProfile().getId();
            boolean bl = playerListEntry.getPublicKeyData() != null;
            map.put(uUID2, new SocialInteractionsPlayerListEntry(this.client, this.parent, uUID2, playerListEntry.getProfile().getName(), playerListEntry::getSkinTexture, bl));
        }
    }

    private void markOfflineMembers(Map<UUID, SocialInteractionsPlayerListEntry> entries, boolean includeOffline) {
        Collection<GameProfile> collection = this.client.getAbuseReportContext().chatLog().streamBackward().collectSenderProfiles();
        for (GameProfile gameProfile : collection) {
            SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry;
            if (includeOffline) {
                socialInteractionsPlayerListEntry = entries.computeIfAbsent(gameProfile.getId(), uuid -> {
                    SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry = new SocialInteractionsPlayerListEntry(this.client, this.parent, gameProfile.getId(), gameProfile.getName(), Suppliers.memoize(() -> this.client.getSkinProvider().loadSkin(gameProfile)), true);
                    socialInteractionsPlayerListEntry.setOffline(true);
                    return socialInteractionsPlayerListEntry;
                });
            } else {
                socialInteractionsPlayerListEntry = entries.get(gameProfile.getId());
                if (socialInteractionsPlayerListEntry == null) continue;
            }
            socialInteractionsPlayerListEntry.setSentMessage(true);
        }
    }

    private void sortPlayers() {
        this.players.sort(Comparator.comparing(player -> {
            if (player.getUuid().equals(this.client.getSession().getUuidOrNull())) {
                return 0;
            }
            if (player.getUuid().version() == 2) {
                return 3;
            }
            if (player.hasSentMessage()) {
                return 1;
            }
            return 2;
        }).thenComparing(player -> {
            int i = player.getName().codePointAt(0);
            if (i == 95 || i >= 97 && i <= 122 || i >= 65 && i <= 90 || i >= 48 && i <= 57) {
                return 0;
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
            if (!socialInteractionsPlayerListEntry.getUuid().equals(uUID)) continue;
            socialInteractionsPlayerListEntry.setOffline(false);
            return;
        }
        if ((tab == SocialInteractionsScreen.Tab.ALL || this.client.getSocialInteractionsManager().isPlayerMuted(uUID)) && (Strings.isNullOrEmpty(this.currentSearch) || player.getProfile().getName().toLowerCase(Locale.ROOT).contains(this.currentSearch))) {
            SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry;
            boolean bl = player.getPublicKeyData() != null;
            socialInteractionsPlayerListEntry = new SocialInteractionsPlayerListEntry(this.client, this.parent, player.getProfile().getId(), player.getProfile().getName(), player::getSkinTexture, bl);
            this.addEntry(socialInteractionsPlayerListEntry);
            this.players.add(socialInteractionsPlayerListEntry);
        }
    }

    public void setPlayerOffline(UUID uuid) {
        for (SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry : this.players) {
            if (!socialInteractionsPlayerListEntry.getUuid().equals(uuid)) continue;
            socialInteractionsPlayerListEntry.setOffline(true);
            return;
        }
    }
}

