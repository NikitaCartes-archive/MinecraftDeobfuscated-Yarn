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
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

/**
 * Responsible for rendering the player list while the {@linkplain
 * net.minecraft.client.option.GameOptions#keyPlayerList player list
 * key} is pressed.
 * 
 * <p>The current instance used by the client can be obtained by {@code
 * MinecraftClient.getInstance().inGameHud.getPlayerListHud()}.
 */
@Environment(value=EnvType.CLIENT)
public class PlayerListHud
extends DrawableHelper {
    private static final Ordering<PlayerListEntry> ENTRY_ORDERING = Ordering.from(new EntryOrderComparator());
    public static final int MAX_ROWS = 20;
    public static final int HEART_OUTLINE_U = 16;
    public static final int BLINKING_HEART_OUTLINE_U = 25;
    public static final int HEART_U = 52;
    public static final int HALF_HEART_U = 61;
    public static final int GOLDEN_HEART_U = 160;
    public static final int HALF_GOLDEN_HEART_U = 169;
    public static final int BLINKING_HEART_U = 70;
    public static final int BLINKING_HALF_HEART_U = 79;
    private final MinecraftClient client;
    private final InGameHud inGameHud;
    @Nullable
    private Text footer;
    @Nullable
    private Text header;
    /**
     * The time, in milliseconds, when this HUD was last set to visible.
     */
    private long showTime;
    private boolean visible;

    public PlayerListHud(MinecraftClient client, InGameHud inGameHud) {
        this.client = client;
        this.inGameHud = inGameHud;
    }

    /**
     * {@return the player name rendered by this HUD}
     */
    public Text getPlayerName(PlayerListEntry entry) {
        if (entry.getDisplayName() != null) {
            return this.applyGameModeFormatting(entry, entry.getDisplayName().shallowCopy());
        }
        return this.applyGameModeFormatting(entry, Team.decorateName(entry.getScoreboardTeam(), new LiteralText(entry.getProfile().getName())));
    }

    /**
     * {@linkplain net.minecraft.util.Formatting#ITALIC Italicizes} the given text if
     * the given player is in {@linkplain net.minecraft.world.GameMode#SPECTATOR spectator mode}.
     */
    private Text applyGameModeFormatting(PlayerListEntry entry, MutableText name) {
        return entry.getGameMode() == GameMode.SPECTATOR ? name.formatted(Formatting.ITALIC) : name;
    }

    public void setVisible(boolean visible) {
        if (visible && !this.visible) {
            this.showTime = Util.getMeasuringTimeMs();
        }
        this.visible = visible;
    }

    public void render(MatrixStack matrices, int scaledWindowWidth, Scoreboard scoreboard, @Nullable ScoreboardObjective objective) {
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
            k = this.client.textRenderer.getWidth(this.getPlayerName(playerListEntry));
            i = Math.max(i, k);
            if (objective == null || objective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) continue;
            k = this.client.textRenderer.getWidth(" " + scoreboard.getPlayerScore(playerListEntry.getProfile().getName(), objective).getScore());
            j = Math.max(j, k);
        }
        list = list.subList(0, Math.min(list.size(), 80));
        int m = l = list.size();
        k = 1;
        while (m > 20) {
            m = (l + ++k - 1) / k;
        }
        boolean bl2 = bl = this.client.isInSingleplayer() || this.client.getNetworkHandler().getConnection().isEncrypted();
        int n = objective != null ? (objective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS ? 90 : j) : 0;
        int o = Math.min(k * ((bl ? 9 : 0) + i + n + 13), scaledWindowWidth - 50) / k;
        int p = scaledWindowWidth / 2 - (o * k + (k - 1) * 5) / 2;
        int q = 10;
        int r = o * k + (k - 1) * 5;
        List<OrderedText> list2 = null;
        if (this.header != null) {
            list2 = this.client.textRenderer.wrapLines(this.header, scaledWindowWidth - 50);
            for (OrderedText orderedText : list2) {
                r = Math.max(r, this.client.textRenderer.getWidth(orderedText));
            }
        }
        List<OrderedText> list3 = null;
        if (this.footer != null) {
            list3 = this.client.textRenderer.wrapLines(this.footer, scaledWindowWidth - 50);
            for (OrderedText orderedText2 : list3) {
                r = Math.max(r, this.client.textRenderer.getWidth(orderedText2));
            }
        }
        if (list2 != null) {
            PlayerListHud.fill(matrices, scaledWindowWidth / 2 - r / 2 - 1, q - 1, scaledWindowWidth / 2 + r / 2 + 1, q + list2.size() * this.client.textRenderer.fontHeight, Integer.MIN_VALUE);
            for (OrderedText orderedText2 : list2) {
                s = this.client.textRenderer.getWidth(orderedText2);
                this.client.textRenderer.drawWithShadow(matrices, orderedText2, (float)(scaledWindowWidth / 2 - s / 2), (float)q, -1);
                q += this.client.textRenderer.fontHeight;
            }
            ++q;
        }
        PlayerListHud.fill(matrices, scaledWindowWidth / 2 - r / 2 - 1, q - 1, scaledWindowWidth / 2 + r / 2 + 1, q + m * 9, Integer.MIN_VALUE);
        int n2 = this.client.options.getTextBackgroundColor(0x20FFFFFF);
        for (int u = 0; u < l; ++u) {
            int ac;
            int ad;
            s = u / m;
            v = u % m;
            int w = p + s * o + s * 5;
            int x = q + v * 9;
            PlayerListHud.fill(matrices, w, x, w + o, x + 8, n2);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            if (u >= list.size()) continue;
            PlayerListEntry playerListEntry2 = list.get(u);
            GameProfile gameProfile = playerListEntry2.getProfile();
            if (bl) {
                PlayerEntity playerEntity = this.client.world.getPlayerByUuid(gameProfile.getId());
                boolean bl22 = playerEntity != null && LivingEntityRenderer.shouldFlipUpsideDown(playerEntity);
                RenderSystem.setShaderTexture(0, playerListEntry2.getSkinTexture());
                int y = 8 + (bl22 ? 8 : 0);
                int z = 8 * (bl22 ? -1 : 1);
                DrawableHelper.drawTexture(matrices, w, x, 8, 8, 8.0f, y, 8, z, 64, 64);
                if (playerEntity != null && playerEntity.isPartVisible(PlayerModelPart.HAT)) {
                    int aa = 8 + (bl22 ? 8 : 0);
                    int ab = 8 * (bl22 ? -1 : 1);
                    DrawableHelper.drawTexture(matrices, w, x, 8, 8, 40.0f, aa, 8, ab, 64, 64);
                }
                w += 9;
            }
            this.client.textRenderer.drawWithShadow(matrices, this.getPlayerName(playerListEntry2), (float)w, (float)x, playerListEntry2.getGameMode() == GameMode.SPECTATOR ? -1862270977 : -1);
            if (objective != null && playerListEntry2.getGameMode() != GameMode.SPECTATOR && (ad = (ac = w + i + 1) + n) - ac > 5) {
                this.renderScoreboardObjective(objective, x, gameProfile.getName(), ac, ad, playerListEntry2, matrices);
            }
            this.renderLatencyIcon(matrices, o, w - (bl ? 9 : 0), x, playerListEntry2);
        }
        if (list3 != null) {
            PlayerListHud.fill(matrices, scaledWindowWidth / 2 - r / 2 - 1, (q += m * 9 + 1) - 1, scaledWindowWidth / 2 + r / 2 + 1, q + list3.size() * this.client.textRenderer.fontHeight, Integer.MIN_VALUE);
            for (OrderedText orderedText3 : list3) {
                v = this.client.textRenderer.getWidth(orderedText3);
                this.client.textRenderer.drawWithShadow(matrices, orderedText3, (float)(scaledWindowWidth / 2 - v / 2), (float)q, -1);
                q += this.client.textRenderer.fontHeight;
            }
        }
    }

    protected void renderLatencyIcon(MatrixStack matrices, int width, int x, int y, PlayerListEntry entry) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
        boolean i = false;
        int j = entry.getLatency() < 0 ? 5 : (entry.getLatency() < 150 ? 0 : (entry.getLatency() < 300 ? 1 : (entry.getLatency() < 600 ? 2 : (entry.getLatency() < 1000 ? 3 : 4))));
        this.setZOffset(this.getZOffset() + 100);
        this.drawTexture(matrices, x + width - 11, y, 0, 176 + j * 8, 10, 8);
        this.setZOffset(this.getZOffset() - 100);
    }

    private void renderScoreboardObjective(ScoreboardObjective objective, int y, String player, int startX, int endX, PlayerListEntry entry, MatrixStack matrices) {
        int i = objective.getScoreboard().getPlayerScore(player, objective).getScore();
        if (objective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
            boolean bl;
            RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
            long l = Util.getMeasuringTimeMs();
            if (this.showTime == entry.getShowTime()) {
                if (i < entry.getLastHealth()) {
                    entry.setLastHealthTime(l);
                    entry.setBlinkingHeartTime(this.inGameHud.getTicks() + 20);
                } else if (i > entry.getLastHealth()) {
                    entry.setLastHealthTime(l);
                    entry.setBlinkingHeartTime(this.inGameHud.getTicks() + 10);
                }
            }
            if (l - entry.getLastHealthTime() > 1000L || this.showTime != entry.getShowTime()) {
                entry.setLastHealth(i);
                entry.setHealth(i);
                entry.setLastHealthTime(l);
            }
            entry.setShowTime(this.showTime);
            entry.setLastHealth(i);
            int j = MathHelper.ceil((float)Math.max(i, entry.getHealth()) / 2.0f);
            int k = Math.max(MathHelper.ceil(i / 2), Math.max(MathHelper.ceil(entry.getHealth() / 2), 10));
            boolean bl2 = bl = entry.getBlinkingHeartTime() > (long)this.inGameHud.getTicks() && (entry.getBlinkingHeartTime() - (long)this.inGameHud.getTicks()) / 3L % 2L == 1L;
            if (j > 0) {
                int m = MathHelper.floor(Math.min((float)(endX - startX - 4) / (float)k, 9.0f));
                if (m > 3) {
                    int n;
                    for (n = j; n < k; ++n) {
                        this.drawTexture(matrices, startX + n * m, y, bl ? 25 : 16, 0, 9, 9);
                    }
                    for (n = 0; n < j; ++n) {
                        this.drawTexture(matrices, startX + n * m, y, bl ? 25 : 16, 0, 9, 9);
                        if (bl) {
                            if (n * 2 + 1 < entry.getHealth()) {
                                this.drawTexture(matrices, startX + n * m, y, 70, 0, 9, 9);
                            }
                            if (n * 2 + 1 == entry.getHealth()) {
                                this.drawTexture(matrices, startX + n * m, y, 79, 0, 9, 9);
                            }
                        }
                        if (n * 2 + 1 < i) {
                            this.drawTexture(matrices, startX + n * m, y, n >= 10 ? 160 : 52, 0, 9, 9);
                        }
                        if (n * 2 + 1 != i) continue;
                        this.drawTexture(matrices, startX + n * m, y, n >= 10 ? 169 : 61, 0, 9, 9);
                    }
                } else {
                    float f = MathHelper.clamp((float)i / 20.0f, 0.0f, 1.0f);
                    int o = (int)((1.0f - f) * 255.0f) << 16 | (int)(f * 255.0f) << 8;
                    String string = "" + (float)i / 2.0f;
                    if (endX - this.client.textRenderer.getWidth(string + "hp") >= startX) {
                        string = string + "hp";
                    }
                    this.client.textRenderer.drawWithShadow(matrices, string, (float)((endX + startX) / 2 - this.client.textRenderer.getWidth(string) / 2), (float)y, o);
                }
            }
        } else {
            String string2 = "" + Formatting.YELLOW + i;
            this.client.textRenderer.drawWithShadow(matrices, string2, (float)(endX - this.client.textRenderer.getWidth(string2)), (float)y, 0xFFFFFF);
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
        EntryOrderComparator() {
        }

        @Override
        public int compare(PlayerListEntry playerListEntry, PlayerListEntry playerListEntry2) {
            Team team = playerListEntry.getScoreboardTeam();
            Team team2 = playerListEntry2.getScoreboardTeam();
            return ComparisonChain.start().compareTrueFirst(playerListEntry.getGameMode() != GameMode.SPECTATOR, playerListEntry2.getGameMode() != GameMode.SPECTATOR).compare((Comparable<?>)((Object)(team != null ? team.getName() : "")), (Comparable<?>)((Object)(team2 != null ? team2.getName() : ""))).compare(playerListEntry.getProfile().getName(), playerListEntry2.getProfile().getName(), String::compareToIgnoreCase).result();
        }

        @Override
        public /* synthetic */ int compare(Object a, Object b) {
            return this.compare((PlayerListEntry)a, (PlayerListEntry)b);
        }
    }
}

