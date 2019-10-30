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

    public PlayerListHud(MinecraftClient minecraftClient, InGameHud inGameHud) {
        this.client = minecraftClient;
        this.inGameHud = inGameHud;
    }

    public Text getPlayerName(PlayerListEntry playerListEntry) {
        if (playerListEntry.getDisplayName() != null) {
            return playerListEntry.getDisplayName();
        }
        return Team.modifyText(playerListEntry.getScoreboardTeam(), new LiteralText(playerListEntry.getProfile().getName()));
    }

    public void tick(boolean bl) {
        if (bl && !this.visible) {
            this.showTime = Util.getMeasuringTimeMs();
        }
        this.visible = bl;
    }

    public void render(int i, Scoreboard scoreboard, @Nullable ScoreboardObjective scoreboardObjective) {
        int w;
        int t;
        boolean bl;
        int m;
        int l;
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
        List<PlayerListEntry> list = ENTRY_ORDERING.sortedCopy(clientPlayNetworkHandler.getPlayerList());
        int j = 0;
        int k = 0;
        for (PlayerListEntry playerListEntry : list) {
            l = this.client.textRenderer.getStringWidth(this.getPlayerName(playerListEntry).asFormattedString());
            j = Math.max(j, l);
            if (scoreboardObjective == null || scoreboardObjective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) continue;
            l = this.client.textRenderer.getStringWidth(" " + scoreboard.getPlayerScore(playerListEntry.getProfile().getName(), scoreboardObjective).getScore());
            k = Math.max(k, l);
        }
        list = list.subList(0, Math.min(list.size(), 80));
        int n = m = list.size();
        l = 1;
        while (n > 20) {
            n = (m + ++l - 1) / l;
        }
        boolean bl2 = bl = this.client.isInSingleplayer() || this.client.getNetworkHandler().getConnection().isEncrypted();
        int o = scoreboardObjective != null ? (scoreboardObjective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS ? 90 : k) : 0;
        int p = Math.min(l * ((bl ? 9 : 0) + j + o + 13), i - 50) / l;
        int q = i / 2 - (p * l + (l - 1) * 5) / 2;
        int r = 10;
        int s = p * l + (l - 1) * 5;
        List<String> list2 = null;
        if (this.header != null) {
            list2 = this.client.textRenderer.wrapStringToWidthAsList(this.header.asFormattedString(), i - 50);
            for (String string : list2) {
                s = Math.max(s, this.client.textRenderer.getStringWidth(string));
            }
        }
        List<String> list3 = null;
        if (this.footer != null) {
            list3 = this.client.textRenderer.wrapStringToWidthAsList(this.footer.asFormattedString(), i - 50);
            for (String string2 : list3) {
                s = Math.max(s, this.client.textRenderer.getStringWidth(string2));
            }
        }
        if (list2 != null) {
            PlayerListHud.fill(i / 2 - s / 2 - 1, r - 1, i / 2 + s / 2 + 1, r + list2.size() * this.client.textRenderer.fontHeight, Integer.MIN_VALUE);
            for (String string2 : list2) {
                t = this.client.textRenderer.getStringWidth(string2);
                this.client.textRenderer.drawWithShadow(string2, i / 2 - t / 2, r, -1);
                r += this.client.textRenderer.fontHeight;
            }
            ++r;
        }
        PlayerListHud.fill(i / 2 - s / 2 - 1, r - 1, i / 2 + s / 2 + 1, r + n * 9, Integer.MIN_VALUE);
        int n2 = this.client.options.getTextBackgroundColor(0x20FFFFFF);
        for (int v = 0; v < m; ++v) {
            int ad;
            int z;
            t = v / n;
            w = v % n;
            int x = q + t * p + t * 5;
            int y = r + w * 9;
            PlayerListHud.fill(x, y, x + p, y + 8, n2);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.enableAlphaTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            if (v >= list.size()) continue;
            PlayerListEntry playerListEntry2 = list.get(v);
            GameProfile gameProfile = playerListEntry2.getProfile();
            if (bl) {
                PlayerEntity playerEntity = this.client.world.getPlayerByUuid(gameProfile.getId());
                boolean bl22 = playerEntity != null && playerEntity.isSkinOverlayVisible(PlayerModelPart.CAPE) && ("Dinnerbone".equals(gameProfile.getName()) || "Grumm".equals(gameProfile.getName()));
                this.client.getTextureManager().bindTexture(playerListEntry2.getSkinTexture());
                z = 8 + (bl22 ? 8 : 0);
                int aa = 8 * (bl22 ? -1 : 1);
                DrawableHelper.blit(x, y, 8, 8, 8.0f, z, 8, aa, 64, 64);
                if (playerEntity != null && playerEntity.isSkinOverlayVisible(PlayerModelPart.HAT)) {
                    int ab = 8 + (bl22 ? 8 : 0);
                    int ac = 8 * (bl22 ? -1 : 1);
                    DrawableHelper.blit(x, y, 8, 8, 40.0f, ab, 8, ac, 64, 64);
                }
                x += 9;
            }
            String string3 = this.getPlayerName(playerListEntry2).asFormattedString();
            if (playerListEntry2.getGameMode() == GameMode.SPECTATOR) {
                this.client.textRenderer.drawWithShadow((Object)((Object)Formatting.ITALIC) + string3, x, y, -1862270977);
            } else {
                this.client.textRenderer.drawWithShadow(string3, x, y, -1);
            }
            if (scoreboardObjective != null && playerListEntry2.getGameMode() != GameMode.SPECTATOR && (z = (ad = x + j + 1) + o) - ad > 5) {
                this.renderScoreboardObjective(scoreboardObjective, y, gameProfile.getName(), ad, z, playerListEntry2);
            }
            this.renderLatencyIcon(p, x - (bl ? 9 : 0), y, playerListEntry2);
        }
        if (list3 != null) {
            PlayerListHud.fill(i / 2 - s / 2 - 1, (r += n * 9 + 1) - 1, i / 2 + s / 2 + 1, r + list3.size() * this.client.textRenderer.fontHeight, Integer.MIN_VALUE);
            for (String string4 : list3) {
                w = this.client.textRenderer.getStringWidth(string4);
                this.client.textRenderer.drawWithShadow(string4, i / 2 - w / 2, r, -1);
                r += this.client.textRenderer.fontHeight;
            }
        }
    }

    protected void renderLatencyIcon(int i, int j, int k, PlayerListEntry playerListEntry) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
        boolean l = false;
        int m = playerListEntry.getLatency() < 0 ? 5 : (playerListEntry.getLatency() < 150 ? 0 : (playerListEntry.getLatency() < 300 ? 1 : (playerListEntry.getLatency() < 600 ? 2 : (playerListEntry.getLatency() < 1000 ? 3 : 4))));
        this.setBlitOffset(this.getBlitOffset() + 100);
        this.blit(j + i - 11, k, 0, 176 + m * 8, 10, 8);
        this.setBlitOffset(this.getBlitOffset() - 100);
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

    public void setFooter(@Nullable Text text) {
        this.footer = text;
    }

    public void setHeader(@Nullable Text text) {
        this.header = text;
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

        public int method_1926(PlayerListEntry playerListEntry, PlayerListEntry playerListEntry2) {
            Team team = playerListEntry.getScoreboardTeam();
            Team team2 = playerListEntry2.getScoreboardTeam();
            return ComparisonChain.start().compareTrueFirst(playerListEntry.getGameMode() != GameMode.SPECTATOR, playerListEntry2.getGameMode() != GameMode.SPECTATOR).compare((Comparable<?>)((Object)(team != null ? team.getName() : "")), (Comparable<?>)((Object)(team2 != null ? team2.getName() : ""))).compare(playerListEntry.getProfile().getName(), playerListEntry2.getProfile().getName(), String::compareToIgnoreCase).result();
        }

        @Override
        public /* synthetic */ int compare(Object object, Object object2) {
            return this.method_1926((PlayerListEntry)object, (PlayerListEntry)object2);
        }
    }
}

