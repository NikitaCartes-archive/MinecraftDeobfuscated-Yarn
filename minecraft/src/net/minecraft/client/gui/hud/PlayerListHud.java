package net.minecraft.client.gui.hud;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
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

/**
 * Responsible for rendering the player list while the {@linkplain
 * net.minecraft.client.option.GameOptions#keyPlayerList player list
 * key} is pressed.
 * 
 * <p>The current instance used by the client can be obtained by {@code
 * MinecraftClient.getInstance().inGameHud.getPlayerListHud()}.
 */
@Environment(EnvType.CLIENT)
public class PlayerListHud extends DrawableHelper {
	private static final Ordering<PlayerListEntry> ENTRY_ORDERING = Ordering.from(new PlayerListHud.EntryOrderComparator());
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
		return entry.getDisplayName() != null
			? this.applyGameModeFormatting(entry, entry.getDisplayName().shallowCopy())
			: this.applyGameModeFormatting(entry, Team.decorateName(entry.getScoreboardTeam(), new LiteralText(entry.getProfile().getName())));
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
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
		List<PlayerListEntry> list = ENTRY_ORDERING.sortedCopy(clientPlayNetworkHandler.getPlayerList());
		int i = 0;
		int j = 0;

		for (PlayerListEntry playerListEntry : list) {
			int k = this.client.textRenderer.getWidth(this.getPlayerName(playerListEntry));
			i = Math.max(i, k);
			if (objective != null && objective.getRenderType() != ScoreboardCriterion.RenderType.HEARTS) {
				k = this.client.textRenderer.getWidth(" " + scoreboard.getPlayerScore(playerListEntry.getProfile().getName(), objective).getScore());
				j = Math.max(j, k);
			}
		}

		list = list.subList(0, Math.min(list.size(), 80));
		int l = list.size();
		int m = l;

		int k;
		for (k = 1; m > 20; m = (l + k - 1) / k) {
			k++;
		}

		boolean bl = this.client.isInSingleplayer() || this.client.getNetworkHandler().getConnection().isEncrypted();
		int n;
		if (objective != null) {
			if (objective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
				n = 90;
			} else {
				n = j;
			}
		} else {
			n = 0;
		}

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
			fill(matrices, scaledWindowWidth / 2 - r / 2 - 1, q - 1, scaledWindowWidth / 2 + r / 2 + 1, q + list2.size() * 9, Integer.MIN_VALUE);

			for (OrderedText orderedText2 : list2) {
				int s = this.client.textRenderer.getWidth(orderedText2);
				this.client.textRenderer.drawWithShadow(matrices, orderedText2, (float)(scaledWindowWidth / 2 - s / 2), (float)q, -1);
				q += 9;
			}

			q++;
		}

		fill(matrices, scaledWindowWidth / 2 - r / 2 - 1, q - 1, scaledWindowWidth / 2 + r / 2 + 1, q + m * 9, Integer.MIN_VALUE);
		int t = this.client.options.getTextBackgroundColor(553648127);

		for (int u = 0; u < l; u++) {
			int s = u / m;
			int v = u % m;
			int w = p + s * o + s * 5;
			int x = q + v * 9;
			fill(matrices, w, x, w + o, x + 8, t);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			if (u < list.size()) {
				PlayerListEntry playerListEntry2 = (PlayerListEntry)list.get(u);
				GameProfile gameProfile = playerListEntry2.getProfile();
				if (bl) {
					PlayerEntity playerEntity = this.client.world.getPlayerByUuid(gameProfile.getId());
					boolean bl2 = playerEntity != null && LivingEntityRenderer.shouldFlipUpsideDown(playerEntity);
					RenderSystem.setShaderTexture(0, playerListEntry2.getSkinTexture());
					int y = 8 + (bl2 ? 8 : 0);
					int z = 8 * (bl2 ? -1 : 1);
					DrawableHelper.drawTexture(matrices, w, x, 8, 8, 8.0F, (float)y, 8, z, 64, 64);
					if (playerEntity != null && playerEntity.isPartVisible(PlayerModelPart.HAT)) {
						int aa = 8 + (bl2 ? 8 : 0);
						int ab = 8 * (bl2 ? -1 : 1);
						DrawableHelper.drawTexture(matrices, w, x, 8, 8, 40.0F, (float)aa, 8, ab, 64, 64);
					}

					w += 9;
				}

				this.client
					.textRenderer
					.drawWithShadow(
						matrices, this.getPlayerName(playerListEntry2), (float)w, (float)x, playerListEntry2.getGameMode() == GameMode.SPECTATOR ? -1862270977 : -1
					);
				if (objective != null && playerListEntry2.getGameMode() != GameMode.SPECTATOR) {
					int ac = w + i + 1;
					int ad = ac + n;
					if (ad - ac > 5) {
						this.renderScoreboardObjective(objective, x, gameProfile.getName(), ac, ad, playerListEntry2, matrices);
					}
				}

				this.renderLatencyIcon(matrices, o, w - (bl ? 9 : 0), x, playerListEntry2);
			}
		}

		if (list3 != null) {
			q += m * 9 + 1;
			fill(matrices, scaledWindowWidth / 2 - r / 2 - 1, q - 1, scaledWindowWidth / 2 + r / 2 + 1, q + list3.size() * 9, Integer.MIN_VALUE);

			for (OrderedText orderedText3 : list3) {
				int v = this.client.textRenderer.getWidth(orderedText3);
				this.client.textRenderer.drawWithShadow(matrices, orderedText3, (float)(scaledWindowWidth / 2 - v / 2), (float)q, -1);
				q += 9;
			}
		}
	}

	protected void renderLatencyIcon(MatrixStack matrices, int width, int x, int y, PlayerListEntry entry) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
		int i = 0;
		int j;
		if (entry.getLatency() < 0) {
			j = 5;
		} else if (entry.getLatency() < 150) {
			j = 0;
		} else if (entry.getLatency() < 300) {
			j = 1;
		} else if (entry.getLatency() < 600) {
			j = 2;
		} else if (entry.getLatency() < 1000) {
			j = 3;
		} else {
			j = 4;
		}

		this.setZOffset(this.getZOffset() + 100);
		this.drawTexture(matrices, x + width - 11, y, 0, 176 + j * 8, 10, 8);
		this.setZOffset(this.getZOffset() - 100);
	}

	private void renderScoreboardObjective(ScoreboardObjective objective, int y, String player, int startX, int endX, PlayerListEntry entry, MatrixStack matrices) {
		int i = objective.getScoreboard().getPlayerScore(player, objective).getScore();
		if (objective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
			RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
			long l = Util.getMeasuringTimeMs();
			if (this.showTime == entry.getShowTime()) {
				if (i < entry.getLastHealth()) {
					entry.setLastHealthTime(l);
					entry.setBlinkingHeartTime((long)(this.inGameHud.getTicks() + 20));
				} else if (i > entry.getLastHealth()) {
					entry.setLastHealthTime(l);
					entry.setBlinkingHeartTime((long)(this.inGameHud.getTicks() + 10));
				}
			}

			if (l - entry.getLastHealthTime() > 1000L || this.showTime != entry.getShowTime()) {
				entry.setLastHealth(i);
				entry.setHealth(i);
				entry.setLastHealthTime(l);
			}

			entry.setShowTime(this.showTime);
			entry.setLastHealth(i);
			int j = MathHelper.ceil((float)Math.max(i, entry.getHealth()) / 2.0F);
			int k = Math.max(MathHelper.ceil((float)(i / 2)), Math.max(MathHelper.ceil((float)(entry.getHealth() / 2)), 10));
			boolean bl = entry.getBlinkingHeartTime() > (long)this.inGameHud.getTicks()
				&& (entry.getBlinkingHeartTime() - (long)this.inGameHud.getTicks()) / 3L % 2L == 1L;
			if (j > 0) {
				int m = MathHelper.floor(Math.min((float)(endX - startX - 4) / (float)k, 9.0F));
				if (m > 3) {
					for (int n = j; n < k; n++) {
						this.drawTexture(matrices, startX + n * m, y, bl ? 25 : 16, 0, 9, 9);
					}

					for (int n = 0; n < j; n++) {
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

						if (n * 2 + 1 == i) {
							this.drawTexture(matrices, startX + n * m, y, n >= 10 ? 169 : 61, 0, 9, 9);
						}
					}
				} else {
					float f = MathHelper.clamp((float)i / 20.0F, 0.0F, 1.0F);
					int o = (int)((1.0F - f) * 255.0F) << 16 | (int)(f * 255.0F) << 8;
					String string = (float)i / 2.0F + "";
					if (endX - this.client.textRenderer.getWidth(string + "hp") >= startX) {
						string = string + "hp";
					}

					this.client.textRenderer.drawWithShadow(matrices, string, (float)((endX + startX) / 2 - this.client.textRenderer.getWidth(string) / 2), (float)y, o);
				}
			}
		} else {
			String string2 = "" + Formatting.YELLOW + i;
			this.client.textRenderer.drawWithShadow(matrices, string2, (float)(endX - this.client.textRenderer.getWidth(string2)), (float)y, 16777215);
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

	@Environment(EnvType.CLIENT)
	static class EntryOrderComparator implements Comparator<PlayerListEntry> {
		public int compare(PlayerListEntry playerListEntry, PlayerListEntry playerListEntry2) {
			Team team = playerListEntry.getScoreboardTeam();
			Team team2 = playerListEntry2.getScoreboardTeam();
			return ComparisonChain.start()
				.compareTrueFirst(playerListEntry.getGameMode() != GameMode.SPECTATOR, playerListEntry2.getGameMode() != GameMode.SPECTATOR)
				.compare(team != null ? team.getName() : "", team2 != null ? team2.getName() : "")
				.compare(playerListEntry.getProfile().getName(), playerListEntry2.getProfile().getName(), String::compareToIgnoreCase)
				.result();
		}
	}
}
