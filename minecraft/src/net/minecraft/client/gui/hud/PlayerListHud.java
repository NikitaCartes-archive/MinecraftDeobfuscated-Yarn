package net.minecraft.client.gui.hud;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;

/**
 * Responsible for rendering the player list while the {@linkplain
 * net.minecraft.client.option.GameOptions#playerListKey player list
 * key} is pressed.
 * 
 * <p>The current instance used by the client can be obtained by {@code
 * MinecraftClient.getInstance().inGameHud.getPlayerListHud()}.
 */
@Environment(EnvType.CLIENT)
public class PlayerListHud extends DrawableHelper {
	private static final Comparator<PlayerListEntry> ENTRY_ORDERING = Comparator.comparingInt(entry -> entry.getGameMode() == GameMode.SPECTATOR ? 1 : 0)
		.thenComparing(entry -> Util.mapOrElse(entry.getScoreboardTeam(), Team::getName, ""))
		.thenComparing(entry -> entry.getProfile().getName(), String::compareToIgnoreCase);
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
	private boolean visible;
	private final Map<UUID, PlayerListHud.Heart> hearts = new Object2ObjectOpenHashMap<>();

	public PlayerListHud(MinecraftClient client, InGameHud inGameHud) {
		this.client = client;
		this.inGameHud = inGameHud;
	}

	/**
	 * {@return the player name rendered by this HUD}
	 */
	public Text getPlayerName(PlayerListEntry entry) {
		return entry.getDisplayName() != null
			? this.applyGameModeFormatting(entry, entry.getDisplayName().copy())
			: this.applyGameModeFormatting(entry, Team.decorateName(entry.getScoreboardTeam(), Text.literal(entry.getProfile().getName())));
	}

	/**
	 * {@linkplain net.minecraft.util.Formatting#ITALIC Italicizes} the given text if
	 * the given player is in {@linkplain net.minecraft.world.GameMode#SPECTATOR spectator mode}.
	 */
	private Text applyGameModeFormatting(PlayerListEntry entry, MutableText name) {
		return entry.getGameMode() == GameMode.SPECTATOR ? name.formatted(Formatting.ITALIC) : name;
	}

	public void setVisible(boolean visible) {
		if (this.visible != visible) {
			this.hearts.clear();
			this.visible = visible;
			if (visible) {
				Text text = Texts.join(this.collectPlayerEntries(), Text.literal(", "), this::getPlayerName);
				this.client.getNarratorManager().narrate(Text.translatable("multiplayer.player.list.narration", text));
			}
		}
	}

	private List<PlayerListEntry> collectPlayerEntries() {
		return this.client.player.networkHandler.getListedPlayerListEntries().stream().sorted(ENTRY_ORDERING).limit(80L).toList();
	}

	public void render(MatrixStack matrices, int scaledWindowWidth, Scoreboard scoreboard, @Nullable ScoreboardObjective objective) {
		List<PlayerListEntry> list = this.collectPlayerEntries();
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

		if (!this.hearts.isEmpty()) {
			Set<UUID> set = (Set<UUID>)list.stream().map(playerEntry -> playerEntry.getProfile().getId()).collect(Collectors.toSet());
			this.hearts.keySet().removeIf(uuid -> !set.contains(uuid));
		}

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
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			if (u < list.size()) {
				PlayerListEntry playerListEntry2 = (PlayerListEntry)list.get(u);
				GameProfile gameProfile = playerListEntry2.getProfile();
				if (bl) {
					PlayerEntity playerEntity = this.client.world.getPlayerByUuid(gameProfile.getId());
					boolean bl2 = playerEntity != null && LivingEntityRenderer.shouldFlipUpsideDown(playerEntity);
					boolean bl3 = playerEntity != null && playerEntity.isPartVisible(PlayerModelPart.HAT);
					RenderSystem.setShaderTexture(0, playerListEntry2.getSkinTexture());
					PlayerSkinDrawer.draw(matrices, w, x, 8, bl3, bl2);
					w += 9;
				}

				this.client
					.textRenderer
					.drawWithShadow(
						matrices, this.getPlayerName(playerListEntry2), (float)w, (float)x, playerListEntry2.getGameMode() == GameMode.SPECTATOR ? -1862270977 : -1
					);
				if (objective != null && playerListEntry2.getGameMode() != GameMode.SPECTATOR) {
					int y = w + i + 1;
					int z = y + n;
					if (z - y > 5) {
						this.renderScoreboardObjective(objective, x, gameProfile.getName(), y, z, gameProfile.getId(), matrices);
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

	private void renderScoreboardObjective(ScoreboardObjective objective, int y, String player, int left, int right, UUID uuid, MatrixStack matrices) {
		int i = objective.getScoreboard().getPlayerScore(player, objective).getScore();
		if (objective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
			this.renderHearts(y, left, right, uuid, matrices, i);
		} else {
			String string = "" + Formatting.YELLOW + i;
			this.client.textRenderer.drawWithShadow(matrices, string, (float)(right - this.client.textRenderer.getWidth(string)), (float)y, 16777215);
		}
	}

	private void renderHearts(int y, int left, int right, UUID uuid, MatrixStack matrices, int score) {
		PlayerListHud.Heart heart = (PlayerListHud.Heart)this.hearts.computeIfAbsent(uuid, uuid2 -> new PlayerListHud.Heart(score));
		heart.tick(score, (long)this.inGameHud.getTicks());
		int i = MathHelper.ceilDiv(Math.max(score, heart.getPrevScore()), 2);
		int j = Math.max(score, Math.max(heart.getPrevScore(), 20)) / 2;
		boolean bl = heart.useHighlighted((long)this.inGameHud.getTicks());
		if (i > 0) {
			RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
			int k = MathHelper.floor(Math.min((float)(right - left - 4) / (float)j, 9.0F));
			if (k <= 3) {
				float f = MathHelper.clamp((float)score / 20.0F, 0.0F, 1.0F);
				int l = (int)((1.0F - f) * 255.0F) << 16 | (int)(f * 255.0F) << 8;
				String string = (float)score / 2.0F + "";
				if (right - this.client.textRenderer.getWidth(string + "hp") >= left) {
					string = string + "hp";
				}

				this.client.textRenderer.drawWithShadow(matrices, string, (float)((right + left - this.client.textRenderer.getWidth(string)) / 2), (float)y, l);
			} else {
				for (int m = i; m < j; m++) {
					this.drawTexture(matrices, left + m * k, y, bl ? 25 : 16, 0, 9, 9);
				}

				for (int m = 0; m < i; m++) {
					this.drawTexture(matrices, left + m * k, y, bl ? 25 : 16, 0, 9, 9);
					if (bl) {
						if (m * 2 + 1 < heart.getPrevScore()) {
							this.drawTexture(matrices, left + m * k, y, 70, 0, 9, 9);
						}

						if (m * 2 + 1 == heart.getPrevScore()) {
							this.drawTexture(matrices, left + m * k, y, 79, 0, 9, 9);
						}
					}

					if (m * 2 + 1 < score) {
						this.drawTexture(matrices, left + m * k, y, m >= 10 ? 160 : 52, 0, 9, 9);
					}

					if (m * 2 + 1 == score) {
						this.drawTexture(matrices, left + m * k, y, m >= 10 ? 169 : 61, 0, 9, 9);
					}
				}
			}
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
	static class Heart {
		private static final long COOLDOWN_TICKS = 20L;
		private static final long SCORE_DECREASE_HIGHLIGHT_TICKS = 20L;
		private static final long SCORE_INCREASE_HIGHLIGHT_TICKS = 10L;
		private int score;
		private int prevScore;
		private long lastScoreChangeTick;
		private long highlightEndTick;

		public Heart(int score) {
			this.prevScore = score;
			this.score = score;
		}

		public void tick(int score, long currentTick) {
			if (score != this.score) {
				long l = score < this.score ? 20L : 10L;
				this.highlightEndTick = currentTick + l;
				this.score = score;
				this.lastScoreChangeTick = currentTick;
			}

			if (currentTick - this.lastScoreChangeTick > 20L) {
				this.prevScore = score;
			}
		}

		public int getPrevScore() {
			return this.prevScore;
		}

		public boolean useHighlighted(long currentTick) {
			return this.highlightEndTick > currentTick && (this.highlightEndTick - currentTick) % 6L >= 3L;
		}
	}
}
