package net.minecraft.client.gui.hud;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ScoreboardEntry;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardTeam;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class ScoreboardHud extends DrawableHelper {
	private static final Ordering<ScoreboardEntry> ENTRY_ORDERING = Ordering.from(new ScoreboardHud.EntryOrderComparator());
	private final MinecraftClient client;
	private final InGameHud inGameHud;
	private TextComponent field_2154;
	private TextComponent field_2153;
	private long field_2152;
	private boolean field_2158;

	public ScoreboardHud(MinecraftClient minecraftClient, InGameHud inGameHud) {
		this.client = minecraftClient;
		this.inGameHud = inGameHud;
	}

	public TextComponent method_1918(ScoreboardEntry scoreboardEntry) {
		return scoreboardEntry.method_2971() != null
			? scoreboardEntry.method_2971()
			: ScoreboardTeam.method_1142(scoreboardEntry.getScoreboardTeam(), new StringTextComponent(scoreboardEntry.getProfile().getName()));
	}

	public void method_1921(boolean bl) {
		if (bl && !this.field_2158) {
			this.field_2152 = SystemUtil.getMeasuringTimeMs();
		}

		this.field_2158 = bl;
	}

	public void method_1919(int i, Scoreboard scoreboard, @Nullable ScoreboardObjective scoreboardObjective) {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.field_1724.networkHandler;
		List<ScoreboardEntry> list = ENTRY_ORDERING.sortedCopy(clientPlayNetworkHandler.getScoreboardEntries());
		int j = 0;
		int k = 0;

		for (ScoreboardEntry scoreboardEntry : list) {
			int l = this.client.field_1772.getStringWidth(this.method_1918(scoreboardEntry).getFormattedText());
			j = Math.max(j, l);
			if (scoreboardObjective != null && scoreboardObjective.method_1118() != ScoreboardCriterion.Type.HEARTS) {
				l = this.client.field_1772.getStringWidth(" " + scoreboard.getPlayerScore(scoreboardEntry.getProfile().getName(), scoreboardObjective).getScore());
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

		boolean bl = this.client.isInSingleplayer() || this.client.method_1562().method_2872().isEncrypted();
		int o;
		if (scoreboardObjective != null) {
			if (scoreboardObjective.method_1118() == ScoreboardCriterion.Type.HEARTS) {
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
		if (this.field_2153 != null) {
			list2 = this.client.field_1772.wrapStringToWidthAsList(this.field_2153.getFormattedText(), i - 50);

			for (String string : list2) {
				s = Math.max(s, this.client.field_1772.getStringWidth(string));
			}
		}

		List<String> list3 = null;
		if (this.field_2154 != null) {
			list3 = this.client.field_1772.wrapStringToWidthAsList(this.field_2154.getFormattedText(), i - 50);

			for (String string2 : list3) {
				s = Math.max(s, this.client.field_1772.getStringWidth(string2));
			}
		}

		if (list2 != null) {
			drawRect(i / 2 - s / 2 - 1, r - 1, i / 2 + s / 2 + 1, r + list2.size() * 9, Integer.MIN_VALUE);

			for (String string2 : list2) {
				int t = this.client.field_1772.getStringWidth(string2);
				this.client.field_1772.drawWithShadow(string2, (float)(i / 2 - t / 2), (float)r, -1);
				r += 9;
			}

			r++;
		}

		drawRect(i / 2 - s / 2 - 1, r - 1, i / 2 + s / 2 + 1, r + n * 9, Integer.MIN_VALUE);
		int u = this.client.field_1690.method_19344(553648127);

		for (int v = 0; v < m; v++) {
			int t = v / n;
			int w = v % n;
			int x = q + t * p + t * 5;
			int y = r + w * 9;
			drawRect(x, y, x + p, y + 8, u);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableAlphaTest();
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
			);
			if (v < list.size()) {
				ScoreboardEntry scoreboardEntry2 = (ScoreboardEntry)list.get(v);
				GameProfile gameProfile = scoreboardEntry2.getProfile();
				if (bl) {
					PlayerEntity playerEntity = this.client.field_1687.method_18470(gameProfile.getId());
					boolean bl2 = playerEntity != null
						&& playerEntity.method_7348(PlayerModelPart.CAPE)
						&& ("Dinnerbone".equals(gameProfile.getName()) || "Grumm".equals(gameProfile.getName()));
					this.client.method_1531().method_4618(scoreboardEntry2.method_2968());
					int z = 8 + (bl2 ? 8 : 0);
					int aa = 8 * (bl2 ? -1 : 1);
					DrawableHelper.drawTexturedRect(x, y, 8.0F, (float)z, 8, aa, 8, 8, 64.0F, 64.0F);
					if (playerEntity != null && playerEntity.method_7348(PlayerModelPart.HEAD)) {
						int ab = 8 + (bl2 ? 8 : 0);
						int ac = 8 * (bl2 ? -1 : 1);
						DrawableHelper.drawTexturedRect(x, y, 40.0F, (float)ab, 8, ac, 8, 8, 64.0F, 64.0F);
					}

					x += 9;
				}

				String string3 = this.method_1918(scoreboardEntry2).getFormattedText();
				if (scoreboardEntry2.getGameMode() == GameMode.field_9219) {
					this.client.field_1772.drawWithShadow(TextFormat.field_1056 + string3, (float)x, (float)y, -1862270977);
				} else {
					this.client.field_1772.drawWithShadow(string3, (float)x, (float)y, -1);
				}

				if (scoreboardObjective != null && scoreboardEntry2.getGameMode() != GameMode.field_9219) {
					int ad = x + j + 1;
					int z = ad + o;
					if (z - ad > 5) {
						this.method_1922(scoreboardObjective, y, gameProfile.getName(), ad, z, scoreboardEntry2);
					}
				}

				this.method_1923(p, x - (bl ? 9 : 0), y, scoreboardEntry2);
			}
		}

		if (list3 != null) {
			r += n * 9 + 1;
			drawRect(i / 2 - s / 2 - 1, r - 1, i / 2 + s / 2 + 1, r + list3.size() * 9, Integer.MIN_VALUE);

			for (String string4 : list3) {
				int w = this.client.field_1772.getStringWidth(string4);
				this.client.field_1772.drawWithShadow(string4, (float)(i / 2 - w / 2), (float)r, -1);
				r += 9;
			}
		}
	}

	protected void method_1923(int i, int j, int k, ScoreboardEntry scoreboardEntry) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.method_1531().method_4618(field_2053);
		int l = 0;
		int m;
		if (scoreboardEntry.getLatency() < 0) {
			m = 5;
		} else if (scoreboardEntry.getLatency() < 150) {
			m = 0;
		} else if (scoreboardEntry.getLatency() < 300) {
			m = 1;
		} else if (scoreboardEntry.getLatency() < 600) {
			m = 2;
		} else if (scoreboardEntry.getLatency() < 1000) {
			m = 3;
		} else {
			m = 4;
		}

		this.zOffset += 100.0F;
		this.drawTexturedRect(j + i - 11, k, 0, 176 + m * 8, 10, 8);
		this.zOffset -= 100.0F;
	}

	private void method_1922(ScoreboardObjective scoreboardObjective, int i, String string, int j, int k, ScoreboardEntry scoreboardEntry) {
		int l = scoreboardObjective.method_1117().getPlayerScore(string, scoreboardObjective).getScore();
		if (scoreboardObjective.method_1118() == ScoreboardCriterion.Type.HEARTS) {
			this.client.method_1531().method_4618(field_2053);
			long m = SystemUtil.getMeasuringTimeMs();
			if (this.field_2152 == scoreboardEntry.method_2976()) {
				if (l < scoreboardEntry.method_2973()) {
					scoreboardEntry.method_2978(m);
					scoreboardEntry.method_2975((long)(this.inGameHud.getTicks() + 20));
				} else if (l > scoreboardEntry.method_2973()) {
					scoreboardEntry.method_2978(m);
					scoreboardEntry.method_2975((long)(this.inGameHud.getTicks() + 10));
				}
			}

			if (m - scoreboardEntry.method_2974() > 1000L || this.field_2152 != scoreboardEntry.method_2976()) {
				scoreboardEntry.method_2972(l);
				scoreboardEntry.method_2965(l);
				scoreboardEntry.method_2978(m);
			}

			scoreboardEntry.method_2964(this.field_2152);
			scoreboardEntry.method_2972(l);
			int n = MathHelper.ceil((float)Math.max(l, scoreboardEntry.method_2960()) / 2.0F);
			int o = Math.max(MathHelper.ceil((float)(l / 2)), Math.max(MathHelper.ceil((float)(scoreboardEntry.method_2960() / 2)), 10));
			boolean bl = scoreboardEntry.method_2961() > (long)this.inGameHud.getTicks()
				&& (scoreboardEntry.method_2961() - (long)this.inGameHud.getTicks()) / 3L % 2L == 1L;
			if (n > 0) {
				float f = Math.min((float)(k - j - 4) / (float)o, 9.0F);
				if (f > 3.0F) {
					for (int p = n; p < o; p++) {
						this.drawTexturedRect((float)j + (float)p * f, (float)i, bl ? 25 : 16, 0, 9, 9);
					}

					for (int p = 0; p < n; p++) {
						this.drawTexturedRect((float)j + (float)p * f, (float)i, bl ? 25 : 16, 0, 9, 9);
						if (bl) {
							if (p * 2 + 1 < scoreboardEntry.method_2960()) {
								this.drawTexturedRect((float)j + (float)p * f, (float)i, 70, 0, 9, 9);
							}

							if (p * 2 + 1 == scoreboardEntry.method_2960()) {
								this.drawTexturedRect((float)j + (float)p * f, (float)i, 79, 0, 9, 9);
							}
						}

						if (p * 2 + 1 < l) {
							this.drawTexturedRect((float)j + (float)p * f, (float)i, p >= 10 ? 160 : 52, 0, 9, 9);
						}

						if (p * 2 + 1 == l) {
							this.drawTexturedRect((float)j + (float)p * f, (float)i, p >= 10 ? 169 : 61, 0, 9, 9);
						}
					}
				} else {
					float g = MathHelper.clamp((float)l / 20.0F, 0.0F, 1.0F);
					int q = (int)((1.0F - g) * 255.0F) << 16 | (int)(g * 255.0F) << 8;
					String string2 = "" + (float)l / 2.0F;
					if (k - this.client.field_1772.getStringWidth(string2 + "hp") >= j) {
						string2 = string2 + "hp";
					}

					this.client.field_1772.drawWithShadow(string2, (float)((k + j) / 2 - this.client.field_1772.getStringWidth(string2) / 2), (float)i, q);
				}
			}
		} else {
			String string3 = TextFormat.field_1054 + "" + l;
			this.client.field_1772.drawWithShadow(string3, (float)(k - this.client.field_1772.getStringWidth(string3)), (float)i, 16777215);
		}
	}

	public void method_1924(@Nullable TextComponent textComponent) {
		this.field_2154 = textComponent;
	}

	public void method_1925(@Nullable TextComponent textComponent) {
		this.field_2153 = textComponent;
	}

	public void clear() {
		this.field_2153 = null;
		this.field_2154 = null;
	}

	@Environment(EnvType.CLIENT)
	static class EntryOrderComparator implements Comparator<ScoreboardEntry> {
		private EntryOrderComparator() {
		}

		public int method_1926(ScoreboardEntry scoreboardEntry, ScoreboardEntry scoreboardEntry2) {
			ScoreboardTeam scoreboardTeam = scoreboardEntry.getScoreboardTeam();
			ScoreboardTeam scoreboardTeam2 = scoreboardEntry2.getScoreboardTeam();
			return ComparisonChain.start()
				.compareTrueFirst(scoreboardEntry.getGameMode() != GameMode.field_9219, scoreboardEntry2.getGameMode() != GameMode.field_9219)
				.compare(scoreboardTeam != null ? scoreboardTeam.getName() : "", scoreboardTeam2 != null ? scoreboardTeam2.getName() : "")
				.compare(scoreboardEntry.getProfile().getName(), scoreboardEntry2.getProfile().getName(), String::compareToIgnoreCase)
				.result();
		}
	}
}
