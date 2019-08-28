package net.minecraft.client.gui.hud;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.SystemUtil;
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

	public PlayerListHud(MinecraftClient minecraftClient, InGameHud inGameHud) {
		this.client = minecraftClient;
		this.inGameHud = inGameHud;
	}

	public Text getPlayerName(PlayerListEntry playerListEntry) {
		return playerListEntry.getDisplayName() != null
			? playerListEntry.getDisplayName()
			: Team.modifyText(playerListEntry.getScoreboardTeam(), new LiteralText(playerListEntry.getProfile().getName()));
	}

	public void tick(boolean bl) {
		if (bl && !this.visible) {
			this.showTime = SystemUtil.getMeasuringTimeMs();
		}

		this.visible = bl;
	}

	public void render(int i, Scoreboard scoreboard, @Nullable ScoreboardObjective scoreboardObjective) {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
		List<PlayerListEntry> list = ENTRY_ORDERING.sortedCopy(clientPlayNetworkHandler.getPlayerList());
		int j = 0;
		int k = 0;

		for (PlayerListEntry playerListEntry : list) {
			int l = this.client.textRenderer.getStringWidth(this.getPlayerName(playerListEntry).asFormattedString());
			j = Math.max(j, l);
			if (scoreboardObjective != null && scoreboardObjective.getRenderType() != ScoreboardCriterion.RenderType.HEARTS) {
				l = this.client.textRenderer.getStringWidth(" " + scoreboard.getPlayerScore(playerListEntry.getProfile().getName(), scoreboardObjective).getScore());
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
		if (scoreboardObjective != null) {
			if (scoreboardObjective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
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
			fill(i / 2 - s / 2 - 1, r - 1, i / 2 + s / 2 + 1, r + list2.size() * 9, Integer.MIN_VALUE);

			for (String string2 : list2) {
				int t = this.client.textRenderer.getStringWidth(string2);
				this.client.textRenderer.drawWithShadow(string2, (float)(i / 2 - t / 2), (float)r, -1);
				r += 9;
			}

			r++;
		}

		fill(i / 2 - s / 2 - 1, r - 1, i / 2 + s / 2 + 1, r + n * 9, Integer.MIN_VALUE);
		int u = this.client.options.getTextBackgroundColor(553648127);

		for (int v = 0; v < m; v++) {
			int t = v / n;
			int w = v % n;
			int x = q + t * p + t * 5;
			int y = r + w * 9;
			fill(x, y, x + p, y + 8, u);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.enableAlphaTest();
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(
				GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA, GlStateManager.class_4535.ONE, GlStateManager.class_4534.ZERO
			);
			if (v < list.size()) {
				PlayerListEntry playerListEntry2 = (PlayerListEntry)list.get(v);
				GameProfile gameProfile = playerListEntry2.getProfile();
				if (bl) {
					PlayerEntity playerEntity = this.client.world.getPlayerByUuid(gameProfile.getId());
					boolean bl2 = playerEntity != null
						&& playerEntity.isSkinOverlayVisible(PlayerModelPart.CAPE)
						&& ("Dinnerbone".equals(gameProfile.getName()) || "Grumm".equals(gameProfile.getName()));
					this.client.getTextureManager().bindTexture(playerListEntry2.getSkinTexture());
					int z = 8 + (bl2 ? 8 : 0);
					int aa = 8 * (bl2 ? -1 : 1);
					DrawableHelper.blit(x, y, 8, 8, 8.0F, (float)z, 8, aa, 64, 64);
					if (playerEntity != null && playerEntity.isSkinOverlayVisible(PlayerModelPart.HAT)) {
						int ab = 8 + (bl2 ? 8 : 0);
						int ac = 8 * (bl2 ? -1 : 1);
						DrawableHelper.blit(x, y, 8, 8, 40.0F, (float)ab, 8, ac, 64, 64);
					}

					x += 9;
				}

				String string3 = this.getPlayerName(playerListEntry2).asFormattedString();
				if (playerListEntry2.getGameMode() == GameMode.SPECTATOR) {
					this.client.textRenderer.drawWithShadow(Formatting.ITALIC + string3, (float)x, (float)y, -1862270977);
				} else {
					this.client.textRenderer.drawWithShadow(string3, (float)x, (float)y, -1);
				}

				if (scoreboardObjective != null && playerListEntry2.getGameMode() != GameMode.SPECTATOR) {
					int ad = x + j + 1;
					int z = ad + o;
					if (z - ad > 5) {
						this.renderScoreboardObjective(scoreboardObjective, y, gameProfile.getName(), ad, z, playerListEntry2);
					}
				}

				this.renderLatencyIcon(p, x - (bl ? 9 : 0), y, playerListEntry2);
			}
		}

		if (list3 != null) {
			r += n * 9 + 1;
			fill(i / 2 - s / 2 - 1, r - 1, i / 2 + s / 2 + 1, r + list3.size() * 9, Integer.MIN_VALUE);

			for (String string4 : list3) {
				int w = this.client.textRenderer.getStringWidth(string4);
				this.client.textRenderer.drawWithShadow(string4, (float)(i / 2 - w / 2), (float)r, -1);
				r += 9;
			}
		}
	}

	protected void renderLatencyIcon(int i, int j, int k, PlayerListEntry playerListEntry) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
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

		this.blitOffset += 100;
		this.blit(j + i - 11, k, 0, 176 + m * 8, 10, 8);
		this.blitOffset -= 100;
	}

	private void renderScoreboardObjective(ScoreboardObjective scoreboardObjective, int i, String string, int j, int k, PlayerListEntry playerListEntry) {
		int l = scoreboardObjective.getScoreboard().getPlayerScore(string, scoreboardObjective).getScore();
		if (scoreboardObjective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
			this.client.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
			long m = SystemUtil.getMeasuringTimeMs();
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
						this.blit(j + q * p, i, bl ? 25 : 16, 0, 9, 9);
					}

					for (int q = 0; q < n; q++) {
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

						if (q * 2 + 1 == l) {
							this.blit(j + q * p, i, q >= 10 ? 169 : 61, 0, 9, 9);
						}
					}
				} else {
					float f = MathHelper.clamp((float)l / 20.0F, 0.0F, 1.0F);
					int r = (int)((1.0F - f) * 255.0F) << 16 | (int)(f * 255.0F) << 8;
					String string2 = "" + (float)l / 2.0F;
					if (k - this.client.textRenderer.getStringWidth(string2 + "hp") >= j) {
						string2 = string2 + "hp";
					}

					this.client.textRenderer.drawWithShadow(string2, (float)((k + j) / 2 - this.client.textRenderer.getStringWidth(string2) / 2), (float)i, r);
				}
			}
		} else {
			String string3 = Formatting.YELLOW + "" + l;
			this.client.textRenderer.drawWithShadow(string3, (float)(k - this.client.textRenderer.getStringWidth(string3)), (float)i, 16777215);
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

	@Environment(EnvType.CLIENT)
	static class EntryOrderComparator implements Comparator<PlayerListEntry> {
		private EntryOrderComparator() {
		}

		public int method_1926(PlayerListEntry playerListEntry, PlayerListEntry playerListEntry2) {
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
