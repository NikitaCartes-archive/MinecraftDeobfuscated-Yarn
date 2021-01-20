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

@Environment(EnvType.CLIENT)
public class PlayerListHud extends DrawableHelper {
	private static final Ordering<PlayerListEntry> ENTRY_ORDERING = Ordering.from(new PlayerListHud.EntryOrderComparator());
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

	public Text getPlayerName(PlayerListEntry entry) {
		return entry.getDisplayName() != null
			? this.applyGameModeFormatting(entry, entry.getDisplayName().shallowCopy())
			: this.applyGameModeFormatting(entry, Team.decorateName(entry.getScoreboardTeam(), new LiteralText(entry.getProfile().getName())));
	}

	private Text applyGameModeFormatting(PlayerListEntry entry, MutableText name) {
		return entry.getGameMode() == GameMode.SPECTATOR ? name.formatted(Formatting.ITALIC) : name;
	}

	public void tick(boolean visible) {
		if (visible && !this.visible) {
			this.showTime = Util.getMeasuringTimeMs();
		}

		this.visible = visible;
	}

	public void render(MatrixStack matrices, int i, Scoreboard scoreboard, @Nullable ScoreboardObjective objective) {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
		List<PlayerListEntry> list = ENTRY_ORDERING.sortedCopy(clientPlayNetworkHandler.getPlayerList());
		int j = 0;
		int k = 0;

		for (PlayerListEntry playerListEntry : list) {
			int l = this.client.textRenderer.getWidth(this.getPlayerName(playerListEntry));
			j = Math.max(j, l);
			if (objective != null && objective.getRenderType() != ScoreboardCriterion.RenderType.HEARTS) {
				l = this.client.textRenderer.getWidth(" " + scoreboard.getPlayerScore(playerListEntry.getProfile().getName(), objective).getScore());
				k = Math.max(k, l);
			}
		}

		list = list.subList(0, Math.min(list.size(), 80));
		int m = list.size();
		int n = m;

		int l;
		for (l = 1; n > 20; n = (m + l - 1) / l) {
			l++;
		}

		boolean bl = this.client.isInSingleplayer() || this.client.getNetworkHandler().getConnection().isEncrypted();
		int o;
		if (objective != null) {
			if (objective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
				o = 90;
			} else {
				o = k;
			}
		} else {
			o = 0;
		}

		int p = Math.min(l * ((bl ? 9 : 0) + j + o + 13), i - 50) / l;
		int q = i / 2 - (p * l + (l - 1) * 5) / 2;
		int r = 10;
		int s = p * l + (l - 1) * 5;
		List<OrderedText> list2 = null;
		if (this.header != null) {
			list2 = this.client.textRenderer.wrapLines(this.header, i - 50);

			for (OrderedText orderedText : list2) {
				s = Math.max(s, this.client.textRenderer.getWidth(orderedText));
			}
		}

		List<OrderedText> list3 = null;
		if (this.footer != null) {
			list3 = this.client.textRenderer.wrapLines(this.footer, i - 50);

			for (OrderedText orderedText2 : list3) {
				s = Math.max(s, this.client.textRenderer.getWidth(orderedText2));
			}
		}

		if (list2 != null) {
			fill(matrices, i / 2 - s / 2 - 1, r - 1, i / 2 + s / 2 + 1, r + list2.size() * 9, Integer.MIN_VALUE);

			for (OrderedText orderedText2 : list2) {
				int t = this.client.textRenderer.getWidth(orderedText2);
				this.client.textRenderer.drawWithShadow(matrices, orderedText2, (float)(i / 2 - t / 2), (float)r, -1);
				r += 9;
			}

			r++;
		}

		fill(matrices, i / 2 - s / 2 - 1, r - 1, i / 2 + s / 2 + 1, r + n * 9, Integer.MIN_VALUE);
		int u = this.client.options.getTextBackgroundColor(553648127);

		for (int v = 0; v < m; v++) {
			int t = v / n;
			int w = v % n;
			int x = q + t * p + t * 5;
			int y = r + w * 9;
			fill(matrices, x, y, x + p, y + 8, u);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.enableAlphaTest();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			if (v < list.size()) {
				PlayerListEntry playerListEntry2 = (PlayerListEntry)list.get(v);
				GameProfile gameProfile = playerListEntry2.getProfile();
				if (bl) {
					PlayerEntity playerEntity = this.client.world.getPlayerByUuid(gameProfile.getId());
					boolean bl2 = playerEntity != null
						&& playerEntity.isPartVisible(PlayerModelPart.CAPE)
						&& ("Dinnerbone".equals(gameProfile.getName()) || "Grumm".equals(gameProfile.getName()));
					this.client.getTextureManager().bindTexture(playerListEntry2.getSkinTexture());
					int z = 8 + (bl2 ? 8 : 0);
					int aa = 8 * (bl2 ? -1 : 1);
					DrawableHelper.drawTexture(matrices, x, y, 8, 8, 8.0F, (float)z, 8, aa, 64, 64);
					if (playerEntity != null && playerEntity.isPartVisible(PlayerModelPart.HAT)) {
						int ab = 8 + (bl2 ? 8 : 0);
						int ac = 8 * (bl2 ? -1 : 1);
						DrawableHelper.drawTexture(matrices, x, y, 8, 8, 40.0F, (float)ab, 8, ac, 64, 64);
					}

					x += 9;
				}

				this.client
					.textRenderer
					.drawWithShadow(
						matrices, this.getPlayerName(playerListEntry2), (float)x, (float)y, playerListEntry2.getGameMode() == GameMode.SPECTATOR ? -1862270977 : -1
					);
				if (objective != null && playerListEntry2.getGameMode() != GameMode.SPECTATOR) {
					int ad = x + j + 1;
					int ae = ad + o;
					if (ae - ad > 5) {
						this.renderScoreboardObjective(objective, y, gameProfile.getName(), ad, ae, playerListEntry2, matrices);
					}
				}

				this.renderLatencyIcon(matrices, p, x - (bl ? 9 : 0), y, playerListEntry2);
			}
		}

		if (list3 != null) {
			r += n * 9 + 1;
			fill(matrices, i / 2 - s / 2 - 1, r - 1, i / 2 + s / 2 + 1, r + list3.size() * 9, Integer.MIN_VALUE);

			for (OrderedText orderedText3 : list3) {
				int w = this.client.textRenderer.getWidth(orderedText3);
				this.client.textRenderer.drawWithShadow(matrices, orderedText3, (float)(i / 2 - w / 2), (float)r, -1);
				r += 9;
			}
		}
	}

	protected void renderLatencyIcon(MatrixStack matrices, int i, int j, int k, PlayerListEntry playerListEntry) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(GUI_ICONS_TEXTURE);
		int l = 0;
		int m;
		if (playerListEntry.getLatency() < 0) {
			m = 5;
		} else if (playerListEntry.getLatency() < 150) {
			m = 0;
		} else if (playerListEntry.getLatency() < 300) {
			m = 1;
		} else if (playerListEntry.getLatency() < 600) {
			m = 2;
		} else if (playerListEntry.getLatency() < 1000) {
			m = 3;
		} else {
			m = 4;
		}

		this.setZOffset(this.getZOffset() + 100);
		this.drawTexture(matrices, j + i - 11, k, 0, 176 + m * 8, 10, 8);
		this.setZOffset(this.getZOffset() - 100);
	}

	private void renderScoreboardObjective(
		ScoreboardObjective objective, int i, String string, int j, int k, PlayerListEntry playerListEntry, MatrixStack matrixStack
	) {
		int l = objective.getScoreboard().getPlayerScore(string, objective).getScore();
		if (objective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
			this.client.getTextureManager().bindTexture(GUI_ICONS_TEXTURE);
			long m = Util.getMeasuringTimeMs();
			if (this.showTime == playerListEntry.method_2976()) {
				if (l < playerListEntry.method_2973()) {
					playerListEntry.method_2978(m);
					playerListEntry.method_2975((long)(this.inGameHud.getTicks() + 20));
				} else if (l > playerListEntry.method_2973()) {
					playerListEntry.method_2978(m);
					playerListEntry.method_2975((long)(this.inGameHud.getTicks() + 10));
				}
			}

			if (m - playerListEntry.method_2974() > 1000L || this.showTime != playerListEntry.method_2976()) {
				playerListEntry.method_2972(l);
				playerListEntry.method_2965(l);
				playerListEntry.method_2978(m);
			}

			playerListEntry.method_2964(this.showTime);
			playerListEntry.method_2972(l);
			int n = MathHelper.ceil((float)Math.max(l, playerListEntry.method_2960()) / 2.0F);
			int o = Math.max(MathHelper.ceil((float)(l / 2)), Math.max(MathHelper.ceil((float)(playerListEntry.method_2960() / 2)), 10));
			boolean bl = playerListEntry.method_2961() > (long)this.inGameHud.getTicks()
				&& (playerListEntry.method_2961() - (long)this.inGameHud.getTicks()) / 3L % 2L == 1L;
			if (n > 0) {
				int p = MathHelper.floor(Math.min((float)(k - j - 4) / (float)o, 9.0F));
				if (p > 3) {
					for (int q = n; q < o; q++) {
						this.drawTexture(matrixStack, j + q * p, i, bl ? 25 : 16, 0, 9, 9);
					}

					for (int q = 0; q < n; q++) {
						this.drawTexture(matrixStack, j + q * p, i, bl ? 25 : 16, 0, 9, 9);
						if (bl) {
							if (q * 2 + 1 < playerListEntry.method_2960()) {
								this.drawTexture(matrixStack, j + q * p, i, 70, 0, 9, 9);
							}

							if (q * 2 + 1 == playerListEntry.method_2960()) {
								this.drawTexture(matrixStack, j + q * p, i, 79, 0, 9, 9);
							}
						}

						if (q * 2 + 1 < l) {
							this.drawTexture(matrixStack, j + q * p, i, q >= 10 ? 160 : 52, 0, 9, 9);
						}

						if (q * 2 + 1 == l) {
							this.drawTexture(matrixStack, j + q * p, i, q >= 10 ? 169 : 61, 0, 9, 9);
						}
					}
				} else {
					float f = MathHelper.clamp((float)l / 20.0F, 0.0F, 1.0F);
					int r = (int)((1.0F - f) * 255.0F) << 16 | (int)(f * 255.0F) << 8;
					String string2 = "" + (float)l / 2.0F;
					if (k - this.client.textRenderer.getWidth(string2 + "hp") >= j) {
						string2 = string2 + "hp";
					}

					this.client.textRenderer.drawWithShadow(matrixStack, string2, (float)((k + j) / 2 - this.client.textRenderer.getWidth(string2) / 2), (float)i, r);
				}
			}
		} else {
			String string3 = Formatting.YELLOW + "" + l;
			this.client.textRenderer.drawWithShadow(matrixStack, string3, (float)(k - this.client.textRenderer.getWidth(string3)), (float)i, 16777215);
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
		private EntryOrderComparator() {
		}

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
