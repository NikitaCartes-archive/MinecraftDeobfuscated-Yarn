/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PlayerListHud
extends DrawableHelper {
    private static final Ordering<PlayerListEntry> ENTRY_ORDERING = Ordering.from(new EntryOrderComparator());
    private final MinecraftClient client;
    private final InGameHud inGameHud;
    private Text footer;
    private Text header;
    private long showTime;
    private boolean visible;

    public PlayerListHud(MinecraftClient client, InGameHud inGameHud) {
        this.client = client;
        this.inGameHud = inGameHud;
    }

    public Text getPlayerName(PlayerListEntry playerEntry) {
        if (playerEntry.getDisplayName() != null) {
            return playerEntry.getDisplayName();
        }
        return Team.modifyText(playerEntry.getScoreboardTeam(), new LiteralText(playerEntry.getProfile().getName()));
    }

    public void tick(boolean visible) {
        if (visible && !this.visible) {
            this.showTime = Util.getMeasuringTimeMs();
        }
        this.visible = visible;
    }

    public void render(int width, Scoreboard scoreboard, @Nullable ScoreboardObjective playerListScoreboardObjective) {
        int v;
        int s;
        boolean bl;
        int l;
        int k;
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
        List<PlayerListEntry> list = ENTRY_ORDERING.sortedCopy(clientPlayNetworkHandler.getPlayerList());
        int i = 0;
        int j = 0;
        for (PlayerListEntry playerListEntry : list) {
            k = this.client.textRenderer.getStringWidth(this.getPlayerName(playerListEntry).asFormattedString());
            i = Math.max(i, k);
            if (playerListScoreboardObjective == null || playerListScoreboardObjective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) continue;
            k = this.client.textRenderer.getStringWidth(" " + scoreboard.getPlayerScore(playerListEntry.getProfile().getName(), playerListScoreboardObjective).getScore());
            j = Math.max(j, k);
        }
        list = list.subList(0, Math.min(list.size(), 80));
        int m = l = list.size();
        k = 1;
        while (m > 20) {
            m = (l + ++k - 1) / k;
        }
        boolean bl2 = bl = this.client.isInSingleplayer() || this.client.getNetworkHandler().getConnection().isEncrypted();
        int n = playerListScoreboardObjective != null ? (playerListScoreboardObjective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS ? 90 : j) : 0;
        int o = Math.min(k * ((bl ? 9 : 0) + i + n + 13), width - 50) / k;
        int p = width / 2 - (o * k + (k - 1) * 5) / 2;
        int q = 10;
        int r = o * k + (k - 1) * 5;
        List<String> list2 = null;
        if (this.header != null) {
            list2 = this.client.textRenderer.wrapStringToWidthAsList(this.header.asFormattedString(), width - 50);
            for (String string : list2) {
                r = Math.max(r, this.client.textRenderer.getStringWidth(string));
            }
        }
        List<String> list3 = null;
        if (this.footer != null) {
            list3 = this.client.textRenderer.wrapStringToWidthAsList(this.footer.asFormattedString(), width - 50);
            for (String string2 : list3) {
                r = Math.max(r, this.client.textRenderer.getStringWidth(string2));
            }
        }
        if (list2 != null) {
            PlayerListHud.fill(width / 2 - r / 2 - 1, q - 1, width / 2 + r / 2 + 1, q + list2.size() * this.client.textRenderer.fontHeight, Integer.MIN_VALUE);
            for (String string2 : list2) {
                s = this.client.textRenderer.getStringWidth(string2);
                this.client.textRenderer.drawWithShadow(string2, width / 2 - s / 2, q, -1);
                q += this.client.textRenderer.fontHeight;
            }
            ++q;
        }
        PlayerListHud.fill(width / 2 - r / 2 - 1, q - 1, width / 2 + r / 2 + 1, q + m * 9, Integer.MIN_VALUE);
        int n2 = this.client.options.getTextBackgroundColor(0x20FFFFFF);
        for (int u = 0; u < l; ++u) {
            int ac;
            int y;
            s = u / m;
            v = u % m;
            int w = p + s * o + s * 5;
            int x = q + v * 9;
            PlayerListHud.fill(w, x, w + o, x + 8, n2);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.enableAlphaTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            if (u >= list.size()) continue;
            PlayerListEntry playerListEntry2 = list.get(u);
            GameProfile gameProfile = playerListEntry2.getProfile();
            if (bl) {
                PlayerEntity playerEntity = this.client.world.getPlayerByUuid(gameProfile.getId());
                boolean bl22 = playerEntity != null && playerEntity.isPartVisible(PlayerModelPart.CAPE) && ("Dinnerbone".equals(gameProfile.getName()) || "Grumm".equals(gameProfile.getName()));
                this.client.getTextureManager().bindTexture(playerListEntry2.getSkinTexture());
                y = 8 + (bl22 ? 8 : 0);
                int z = 8 * (bl22 ? -1 : 1);
                DrawableHelper.blit(w, x, 8, 8, 8.0f, y, 8, z, 64, 64);
                if (playerEntity != null && playerEntity.isPartVisible(PlayerModelPart.HAT)) {
                    int aa = 8 + (bl22 ? 8 : 0);
                    int ab = 8 * (bl22 ? -1 : 1);
                    DrawableHelper.blit(w, x, 8, 8, 40.0f, aa, 8, ab, 64, 64);
                }
                w += 9;
            }
            String string3 = this.getPlayerName(playerListEntry2).asFormattedString();
            if (playerListEntry2.getGameMode() == GameMode.SPECTATOR) {
                this.client.textRenderer.drawWithShadow((Object)((Object)Formatting.ITALIC) + string3, w, x, -1862270977);
            } else {
                this.client.textRenderer.drawWithShadow(string3, w, x, -1);
            }
            if (playerListScoreboardObjective != null && playerListEntry2.getGameMode() != GameMode.SPECTATOR && (y = (ac = w + i + 1) + n) - ac > 5) {
                this.renderScoreboardObjective(playerListScoreboardObjective, x, gameProfile.getName(), ac, y, playerListEntry2);
            }
            this.renderLatencyIcon(o, w - (bl ? 9 : 0), x, playerListEntry2);
        }
        if (list3 != null) {
            PlayerListHud.fill(width / 2 - r / 2 - 1, (q += m * 9 + 1) - 1, width / 2 + r / 2 + 1, q + list3.size() * this.client.textRenderer.fontHeight, Integer.MIN_VALUE);
            for (String string4 : list3) {
                v = this.client.textRenderer.getStringWidth(string4);
                this.client.textRenderer.drawWithShadow(string4, width / 2 - v / 2, q, -1);
                q += this.client.textRenderer.fontHeight;
            }
        }
    }

    protected void renderLatencyIcon(int i, int j, int y, PlayerListEntry playerEntry) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
        boolean k = false;
        int l = playerEntry.getLatency() < 0 ? 5 : (playerEntry.getLatency() < 150 ? 0 : (playerEntry.getLatency() < 300 ? 1 : (playerEntry.getLatency() < 600 ? 2 : (playerEntry.getLatency() < 1000 ? 3 : 4))));
        this.setZOffset(this.getZOffset() + 100);
        this.blit(j + i - 11, y, 0, 176 + l * 8, 10, 8);
        this.setZOffset(this.getZOffset() - 100);
    }

    private void renderScoreboardObjective(ScoreboardObjective scoreboardObjective, int i, String string, int j, int k, PlayerListEntry playerListEntry) {
        int l = scoreboardObjective.getScoreboard().getPlayerScore(string, scoreboardObjective).getScore();
        if (scoreboardObjective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
            boolean bl;
            this.client.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
            long m = Util.getMeasuringTimeMs();
            if (this.showTime == playerListEntry.method_2976()) {
                if (l < playerListEntry.method_2973()) {
                    playerListEntry.method_2978(m);
                    playerListEntry.method_2975(this.inGameHud.getTicks() + 20);
                } else if (l > playerListEntry.method_2973()) {
                    playerListEntry.method_2978(m);
                    playerListEntry.method_2975(this.inGameHud.getTicks() + 10);
                }
            }
            if (m - playerListEntry.method_2974() > 1000L || this.showTime != playerListEntry.method_2976()) {
                playerListEntry.method_2972(l);
                playerListEntry.method_2965(l);
                playerListEntry.method_2978(m);
            }
            playerListEntry.method_2964(this.showTime);
            playerListEntry.method_2972(l);
            int n = MathHelper.ceil((float)Math.max(l, playerListEntry.method_2960()) / 2.0f);
            int o = Math.max(MathHelper.ceil(l / 2), Math.max(MathHelper.ceil(playerListEntry.method_2960() / 2), 10));
            boolean bl2 = bl = playerListEntry.method_2961() > (long)this.inGameHud.getTicks() && (playerListEntry.method_2961() - (long)this.inGameHud.getTicks()) / 3L % 2L == 1L;
            if (n > 0) {
                int p = MathHelper.floor(Math.min((float)(k - j - 4) / (float)o, 9.0f));
                if (p > 3) {
                    int q;
                    for (q = n; q < o; ++q) {
                        this.blit(j + q * p, i, bl ? 25 : 16, 0, 9, 9);
                    }
                    for (q = 0; q < n; ++q) {
                        this.blit(j + q * p, i, bl ? 25 : 16, 0, 9, 9);
                        if (bl) {
                            if (q * 2 + 1 < playerListEntry.method_2960()) {
                                this.blit(j + q * p, i, 70, 0, 9, 9);
                            }
                            if (q * 2 + 1 == playerListEntry.method_2960()) {
                                this.blit(j + q * p, i, 79, 0, 9, 9);
                            }
                        }
                        if (q * 2 + 1 < l) {
                            this.blit(j + q * p, i, q >= 10 ? 160 : 52, 0, 9, 9);
                        }
                        if (q * 2 + 1 != l) continue;
                        this.blit(j + q * p, i, q >= 10 ? 169 : 61, 0, 9, 9);
                    }
                } else {
                    float f = MathHelper.clamp((float)l / 20.0f, 0.0f, 1.0f);
                    int r = (int)((1.0f - f) * 255.0f) << 16 | (int)(f * 255.0f) << 8;
                    String string2 = "" + (float)l / 2.0f;
                    if (k - this.client.textRenderer.getStringWidth(string2 + "hp") >= j) {
                        string2 = string2 + "hp";
                    }
                    this.client.textRenderer.drawWithShadow(string2, (k + j) / 2 - this.client.textRenderer.getStringWidth(string2) / 2, i, r);
                }
            }
        } else {
            String string3 = (Object)((Object)Formatting.YELLOW) + "" + l;
            this.client.textRenderer.drawWithShadow(string3, k - this.client.textRenderer.getStringWidth(string3), i, 0xFFFFFF);
        }
    }

    public void setFooter(@Nullable Text footer) {
        this.footer = footer;
    }

    public void setHeader(@Nullable Text header) {
        this.header = header;
    }

    public void clear() {
        this.header = null;
        this.footer = null;
    }

    @Environment(value=EnvType.CLIENT)
    static class EntryOrderComparator
    implements Comparator<PlayerListEntry> {
        private EntryOrderComparator() {
        }

        @Override
        public int compare(PlayerListEntry playerListEntry, PlayerListEntry playerListEntry2) {
            Team team = playerListEntry.getScoreboardTeam();
            Team team2 = playerListEntry2.getScoreboardTeam();
            return ComparisonChain.start().compareTrueFirst(playerListEntry.getGameMode() != GameMode.SPECTATOR, playerListEntry2.getGameMode() != GameMode.SPECTATOR).compare((Comparable<?>)((Object)(team != null ? team.getName() : "")), (Comparable<?>)((Object)(team2 != null ? team2.getName() : ""))).compare(playerListEntry.getProfile().getName(), playerListEntry2.getProfile().getName(), String::compareToIgnoreCase).result();
        }

        @Override
        public /* synthetic */ int compare(Object object, Object object2) {
            return this.compare((PlayerListEntry)object, (PlayerListEntry)object2);
        }
    }
}

