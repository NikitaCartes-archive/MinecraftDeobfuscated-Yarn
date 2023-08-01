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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.Nullables;
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
public class PlayerListHud {
	private static final Identifier PING_UNKNOWN_ICON_TEXTURE = new Identifier("icon/ping_unknown");
	private static final Identifier PING_1_ICON_TEXTURE = new Identifier("icon/ping_1");
	private static final Identifier PING_2_ICON_TEXTURE = new Identifier("icon/ping_2");
	private static final Identifier PING_3_ICON_TEXTURE = new Identifier("icon/ping_3");
	private static final Identifier PING_4_ICON_TEXTURE = new Identifier("icon/ping_4");
	private static final Identifier PING_5_ICON_TEXTURE = new Identifier("icon/ping_5");
	private static final Identifier CONTAINER_HEART_BLINKING_TEXTURE = new Identifier("hud/heart/container_blinking");
	private static final Identifier CONTAINER_HEART_TEXTURE = new Identifier("hud/heart/container");
	private static final Identifier FULL_HEART_BLINKING_TEXTURE = new Identifier("hud/heart/full_blinking");
	private static final Identifier HALF_HEART_BLINKING_TEXTURE = new Identifier("hud/heart/half_blinking");
	private static final Identifier ABSORBING_FULL_HEART_BLINKING_TEXTURE = new Identifier("hud/heart/absorbing_full_blinking");
	private static final Identifier FULL_HEART_TEXTURE = new Identifier("hud/heart/full");
	private static final Identifier ABSORBING_HALF_HEART_BLINKING_TEXTURE = new Identifier("hud/heart/absorbing_half_blinking");
	private static final Identifier HALF_HEART_TEXTURE = new Identifier("hud/heart/half");
	private static final Comparator<PlayerListEntry> ENTRY_ORDERING = Comparator.comparingInt(entry -> entry.getGameMode() == GameMode.SPECTATOR ? 1 : 0)
		.thenComparing(entry -> Nullables.mapOrElse(entry.getScoreboardTeam(), Team::getName, ""))
		.thenComparing(entry -> entry.getProfile().getName(), String::compareToIgnoreCase);
	public static final int MAX_ROWS = 20;
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

	public void render(DrawContext context, int scaledWindowWidth, Scoreboard scoreboard, @Nullable ScoreboardObjective objective) {
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
			context.fill(scaledWindowWidth / 2 - r / 2 - 1, q - 1, scaledWindowWidth / 2 + r / 2 + 1, q + list2.size() * 9, Integer.MIN_VALUE);

			for (OrderedText orderedText2 : list2) {
				int s = this.client.textRenderer.getWidth(orderedText2);
				context.drawTextWithShadow(this.client.textRenderer, orderedText2, scaledWindowWidth / 2 - s / 2, q, -1);
				q += 9;
			}

			q++;
		}

		context.fill(scaledWindowWidth / 2 - r / 2 - 1, q - 1, scaledWindowWidth / 2 + r / 2 + 1, q + m * 9, Integer.MIN_VALUE);
		int t = this.client.options.getTextBackgroundColor(553648127);

		for (int u = 0; u < l; u++) {
			int s = u / m;
			int v = u % m;
			int w = p + s * o + s * 5;
			int x = q + v * 9;
			context.fill(w, x, w + o, x + 8, t);
			RenderSystem.enableBlend();
			if (u < list.size()) {
				PlayerListEntry playerListEntry2 = (PlayerListEntry)list.get(u);
				GameProfile gameProfile = playerListEntry2.getProfile();
				if (bl) {
					PlayerEntity playerEntity = this.client.world.getPlayerByUuid(gameProfile.getId());
					boolean bl2 = playerEntity != null && LivingEntityRenderer.shouldFlipUpsideDown(playerEntity);
					boolean bl3 = playerEntity != null && playerEntity.isPartVisible(PlayerModelPart.HAT);
					PlayerSkinDrawer.draw(context, playerListEntry2.getSkinTextures().texture(), w, x, 8, bl3, bl2);
					w += 9;
				}

				context.drawTextWithShadow(
					this.client.textRenderer, this.getPlayerName(playerListEntry2), w, x, playerListEntry2.getGameMode() == GameMode.SPECTATOR ? -1862270977 : -1
				);
				if (objective != null && playerListEntry2.getGameMode() != GameMode.SPECTATOR) {
					int y = w + i + 1;
					int z = y + n;
					if (z - y > 5) {
						this.renderScoreboardObjective(objective, x, gameProfile.getName(), y, z, gameProfile.getId(), context);
					}
				}

				this.renderLatencyIcon(context, o, w - (bl ? 9 : 0), x, playerListEntry2);
			}
		}

		if (list3 != null) {
			q += m * 9 + 1;
			context.fill(scaledWindowWidth / 2 - r / 2 - 1, q - 1, scaledWindowWidth / 2 + r / 2 + 1, q + list3.size() * 9, Integer.MIN_VALUE);

			for (OrderedText orderedText3 : list3) {
				int v = this.client.textRenderer.getWidth(orderedText3);
				context.drawTextWithShadow(this.client.textRenderer, orderedText3, scaledWindowWidth / 2 - v / 2, q, -1);
				q += 9;
			}
		}
	}

	protected void renderLatencyIcon(DrawContext context, int width, int x, int y, PlayerListEntry entry) {
		Identifier identifier;
		if (entry.getLatency() < 0) {
			identifier = PING_UNKNOWN_ICON_TEXTURE;
		} else if (entry.getLatency() < 150) {
			identifier = PING_5_ICON_TEXTURE;
		} else if (entry.getLatency() < 300) {
			identifier = PING_4_ICON_TEXTURE;
		} else if (entry.getLatency() < 600) {
			identifier = PING_3_ICON_TEXTURE;
		} else if (entry.getLatency() < 1000) {
			identifier = PING_2_ICON_TEXTURE;
		} else {
			identifier = PING_1_ICON_TEXTURE;
		}

		context.getMatrices().push();
		context.getMatrices().translate(0.0F, 0.0F, 100.0F);
		context.drawGuiTexture(identifier, x + width - 11, y, 10, 8);
		context.getMatrices().pop();
	}

	private void renderScoreboardObjective(ScoreboardObjective objective, int y, String player, int left, int right, UUID uuid, DrawContext context) {
		int i = objective.getScoreboard().getPlayerScore(player, objective).getScore();
		if (objective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
			this.renderHearts(y, left, right, uuid, context, i);
		} else {
			String string = "" + Formatting.YELLOW + i;
			context.drawTextWithShadow(this.client.textRenderer, string, right - this.client.textRenderer.getWidth(string), y, 16777215);
		}
	}

	private void renderHearts(int y, int left, int right, UUID uuid, DrawContext context, int score) {
		PlayerListHud.Heart heart = (PlayerListHud.Heart)this.hearts.computeIfAbsent(uuid, uuid2 -> new PlayerListHud.Heart(score));
		heart.tick(score, (long)this.inGameHud.getTicks());
		int i = MathHelper.ceilDiv(Math.max(score, heart.getPrevScore()), 2);
		int j = Math.max(score, Math.max(heart.getPrevScore(), 20)) / 2;
		boolean bl = heart.useHighlighted((long)this.inGameHud.getTicks());
		if (i > 0) {
			int k = MathHelper.floor(Math.min((float)(right - left - 4) / (float)j, 9.0F));
			if (k <= 3) {
				float f = MathHelper.clamp((float)score / 20.0F, 0.0F, 1.0F);
				int l = (int)((1.0F - f) * 255.0F) << 16 | (int)(f * 255.0F) << 8;
				String string = (float)score / 2.0F + "";
				if (right - this.client.textRenderer.getWidth(string + "hp") >= left) {
					string = string + "hp";
				}

				context.drawTextWithShadow(this.client.textRenderer, string, (right + left - this.client.textRenderer.getWidth(string)) / 2, y, l);
			} else {
				Identifier identifier = bl ? CONTAINER_HEART_BLINKING_TEXTURE : CONTAINER_HEART_TEXTURE;

				for (int l = i; l < j; l++) {
					context.drawGuiTexture(identifier, left + l * k, y, 9, 9);
				}

				for (int l = 0; l < i; l++) {
					context.drawGuiTexture(identifier, left + l * k, y, 9, 9);
					if (bl) {
						if (l * 2 + 1 < heart.getPrevScore()) {
							context.drawGuiTexture(FULL_HEART_BLINKING_TEXTURE, left + l * k, y, 9, 9);
						}

						if (l * 2 + 1 == heart.getPrevScore()) {
							context.drawGuiTexture(HALF_HEART_BLINKING_TEXTURE, left + l * k, y, 9, 9);
						}
					}

					if (l * 2 + 1 < score) {
						context.drawGuiTexture(l >= 10 ? ABSORBING_FULL_HEART_BLINKING_TEXTURE : FULL_HEART_TEXTURE, left + l * k, y, 9, 9);
					}

					if (l * 2 + 1 == score) {
						context.drawGuiTexture(l >= 10 ? ABSORBING_HALF_HEART_BLINKING_TEXTURE : HALF_HEART_TEXTURE, left + l * k, y, 9, 9);
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
