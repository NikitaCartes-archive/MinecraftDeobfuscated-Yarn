package net.minecraft.client.gui.screen.advancement;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class AdvancementWidget extends DrawableHelper {
	private static final Identifier WIDGETS_TEX = new Identifier("textures/gui/advancements/widgets.png");
	private static final Pattern field_2708 = Pattern.compile("(.+) \\S+");
	private final AdvancementTreeWidget tree;
	private final Advancement advancement;
	private final AdvancementDisplay display;
	private final String field_2713;
	private final int field_2715;
	private final List<String> field_2705;
	private final MinecraftClient client;
	private AdvancementWidget field_2706;
	private final List<AdvancementWidget> children = Lists.<AdvancementWidget>newArrayList();
	private AdvancementProgress field_2714;
	private final int xPos;
	private final int yPos;

	public AdvancementWidget(
		AdvancementTreeWidget advancementTreeWidget, MinecraftClient minecraftClient, Advancement advancement, AdvancementDisplay advancementDisplay
	) {
		this.tree = advancementTreeWidget;
		this.advancement = advancement;
		this.display = advancementDisplay;
		this.client = minecraftClient;
		this.field_2713 = minecraftClient.field_1772.trimToWidth(advancementDisplay.getTitle().asFormattedString(), 163);
		this.xPos = MathHelper.floor(advancementDisplay.getX() * 28.0F);
		this.yPos = MathHelper.floor(advancementDisplay.getY() * 27.0F);
		int i = advancement.getRequirementCount();
		int j = String.valueOf(i).length();
		int k = i > 1
			? minecraftClient.field_1772.getStringWidth("  ") + minecraftClient.field_1772.getStringWidth("0") * j * 2 + minecraftClient.field_1772.getStringWidth("/")
			: 0;
		int l = 29 + minecraftClient.field_1772.getStringWidth(this.field_2713) + k;
		String string = advancementDisplay.getDescription().asFormattedString();
		this.field_2705 = this.method_2330(string, l);

		for (String string2 : this.field_2705) {
			l = Math.max(l, minecraftClient.field_1772.getStringWidth(string2));
		}

		this.field_2715 = l + 3 + 5;
	}

	private List<String> method_2330(String string, int i) {
		if (string.isEmpty()) {
			return Collections.emptyList();
		} else {
			List<String> list = this.client.field_1772.wrapStringToWidthAsList(string, i);
			if (list.size() < 2) {
				return list;
			} else {
				String string2 = (String)list.get(0);
				String string3 = (String)list.get(1);
				int j = this.client.field_1772.getStringWidth(string2 + ' ' + string3.split(" ")[0]);
				if (j - i <= 10) {
					return this.client.field_1772.wrapStringToWidthAsList(string, j);
				} else {
					Matcher matcher = field_2708.matcher(string2);
					if (matcher.matches()) {
						int k = this.client.field_1772.getStringWidth(matcher.group(1));
						if (i - k <= 10) {
							return this.client.field_1772.wrapStringToWidthAsList(string, k);
						}
					}

					return list;
				}
			}
		}
	}

	@Nullable
	private AdvancementWidget getRootWidget(Advancement advancement) {
		do {
			advancement = advancement.getParent();
		} while (advancement != null && advancement.getDisplay() == null);

		return advancement != null && advancement.getDisplay() != null ? this.tree.method_2308(advancement) : null;
	}

	public void method_2323(int i, int j, boolean bl) {
		if (this.field_2706 != null) {
			int k = i + this.field_2706.xPos + 13;
			int l = i + this.field_2706.xPos + 26 + 4;
			int m = j + this.field_2706.yPos + 13;
			int n = i + this.xPos + 13;
			int o = j + this.yPos + 13;
			int p = bl ? -16777216 : -1;
			if (bl) {
				this.hLine(l, k, m - 1, p);
				this.hLine(l + 1, k, m, p);
				this.hLine(l, k, m + 1, p);
				this.hLine(n, l - 1, o - 1, p);
				this.hLine(n, l - 1, o, p);
				this.hLine(n, l - 1, o + 1, p);
				this.vLine(l - 1, o, m, p);
				this.vLine(l + 1, o, m, p);
			} else {
				this.hLine(l, k, m, p);
				this.hLine(n, l, o, p);
				this.vLine(l, o, m, p);
			}
		}

		for (AdvancementWidget advancementWidget : this.children) {
			advancementWidget.method_2323(i, j, bl);
		}
	}

	public void method_2325(int i, int j) {
		if (!this.display.isHidden() || this.field_2714 != null && this.field_2714.isDone()) {
			float f = this.field_2714 == null ? 0.0F : this.field_2714.getProgressBarPercentage();
			AdvancementObtainedStatus advancementObtainedStatus;
			if (f >= 1.0F) {
				advancementObtainedStatus = AdvancementObtainedStatus.field_2701;
			} else {
				advancementObtainedStatus = AdvancementObtainedStatus.field_2699;
			}

			this.client.method_1531().bindTexture(WIDGETS_TEX);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableBlend();
			this.blit(i + this.xPos + 3, j + this.yPos, this.display.getFrame().texV(), 128 + advancementObtainedStatus.getSpriteIndex() * 26, 26, 26);
			GuiLighting.enableForItems();
			this.client.method_1480().renderGuiItem(null, this.display.getIcon(), i + this.xPos + 8, j + this.yPos + 5);
		}

		for (AdvancementWidget advancementWidget : this.children) {
			advancementWidget.method_2325(i, j);
		}
	}

	public void setProgress(AdvancementProgress advancementProgress) {
		this.field_2714 = advancementProgress;
	}

	public void method_2322(AdvancementWidget advancementWidget) {
		this.children.add(advancementWidget);
	}

	public void method_2331(int i, int j, float f, int k, int l) {
		boolean bl = k + i + this.xPos + this.field_2715 + 26 >= this.tree.method_2312().width;
		String string = this.field_2714 == null ? null : this.field_2714.getProgressBarFraction();
		int m = string == null ? 0 : this.client.field_1772.getStringWidth(string);
		boolean bl2 = 113 - j - this.yPos - 26 <= 6 + this.field_2705.size() * 9;
		float g = this.field_2714 == null ? 0.0F : this.field_2714.getProgressBarPercentage();
		int n = MathHelper.floor(g * (float)this.field_2715);
		AdvancementObtainedStatus advancementObtainedStatus;
		AdvancementObtainedStatus advancementObtainedStatus2;
		AdvancementObtainedStatus advancementObtainedStatus3;
		if (g >= 1.0F) {
			n = this.field_2715 / 2;
			advancementObtainedStatus = AdvancementObtainedStatus.field_2701;
			advancementObtainedStatus2 = AdvancementObtainedStatus.field_2701;
			advancementObtainedStatus3 = AdvancementObtainedStatus.field_2701;
		} else if (n < 2) {
			n = this.field_2715 / 2;
			advancementObtainedStatus = AdvancementObtainedStatus.field_2699;
			advancementObtainedStatus2 = AdvancementObtainedStatus.field_2699;
			advancementObtainedStatus3 = AdvancementObtainedStatus.field_2699;
		} else if (n > this.field_2715 - 2) {
			n = this.field_2715 / 2;
			advancementObtainedStatus = AdvancementObtainedStatus.field_2701;
			advancementObtainedStatus2 = AdvancementObtainedStatus.field_2701;
			advancementObtainedStatus3 = AdvancementObtainedStatus.field_2699;
		} else {
			advancementObtainedStatus = AdvancementObtainedStatus.field_2701;
			advancementObtainedStatus2 = AdvancementObtainedStatus.field_2699;
			advancementObtainedStatus3 = AdvancementObtainedStatus.field_2699;
		}

		int o = this.field_2715 - n;
		this.client.method_1531().bindTexture(WIDGETS_TEX);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableBlend();
		int p = j + this.yPos;
		int q;
		if (bl) {
			q = i + this.xPos - this.field_2715 + 26 + 6;
		} else {
			q = i + this.xPos;
		}

		int r = 32 + this.field_2705.size() * 9;
		if (!this.field_2705.isEmpty()) {
			if (bl2) {
				this.method_2324(q, p + 26 - r, this.field_2715, r, 10, 200, 26, 0, 52);
			} else {
				this.method_2324(q, p, this.field_2715, r, 10, 200, 26, 0, 52);
			}
		}

		this.blit(q, p, 0, advancementObtainedStatus.getSpriteIndex() * 26, n, 26);
		this.blit(q + n, p, 200 - o, advancementObtainedStatus2.getSpriteIndex() * 26, o, 26);
		this.blit(i + this.xPos + 3, j + this.yPos, this.display.getFrame().texV(), 128 + advancementObtainedStatus3.getSpriteIndex() * 26, 26, 26);
		if (bl) {
			this.client.field_1772.drawWithShadow(this.field_2713, (float)(q + 5), (float)(j + this.yPos + 9), -1);
			if (string != null) {
				this.client.field_1772.drawWithShadow(string, (float)(i + this.xPos - m), (float)(j + this.yPos + 9), -1);
			}
		} else {
			this.client.field_1772.drawWithShadow(this.field_2713, (float)(i + this.xPos + 32), (float)(j + this.yPos + 9), -1);
			if (string != null) {
				this.client.field_1772.drawWithShadow(string, (float)(i + this.xPos + this.field_2715 - m - 5), (float)(j + this.yPos + 9), -1);
			}
		}

		if (bl2) {
			for (int s = 0; s < this.field_2705.size(); s++) {
				this.client.field_1772.draw((String)this.field_2705.get(s), (float)(q + 5), (float)(p + 26 - r + 7 + s * 9), -5592406);
			}
		} else {
			for (int s = 0; s < this.field_2705.size(); s++) {
				this.client.field_1772.draw((String)this.field_2705.get(s), (float)(q + 5), (float)(j + this.yPos + 9 + 17 + s * 9), -5592406);
			}
		}

		GuiLighting.enableForItems();
		this.client.method_1480().renderGuiItem(null, this.display.getIcon(), i + this.xPos + 8, j + this.yPos + 5);
	}

	protected void method_2324(int i, int j, int k, int l, int m, int n, int o, int p, int q) {
		this.blit(i, j, p, q, m, m);
		this.method_2321(i + m, j, k - m - m, m, p + m, q, n - m - m, o);
		this.blit(i + k - m, j, p + n - m, q, m, m);
		this.blit(i, j + l - m, p, q + o - m, m, m);
		this.method_2321(i + m, j + l - m, k - m - m, m, p + m, q + o - m, n - m - m, o);
		this.blit(i + k - m, j + l - m, p + n - m, q + o - m, m, m);
		this.method_2321(i, j + m, m, l - m - m, p, q + m, n, o - m - m);
		this.method_2321(i + m, j + m, k - m - m, l - m - m, p + m, q + m, n - m - m, o - m - m);
		this.method_2321(i + k - m, j + m, m, l - m - m, p + n - m, q + m, n, o - m - m);
	}

	protected void method_2321(int i, int j, int k, int l, int m, int n, int o, int p) {
		int q = 0;

		while (q < k) {
			int r = i + q;
			int s = Math.min(o, k - q);
			int t = 0;

			while (t < l) {
				int u = j + t;
				int v = Math.min(p, l - t);
				this.blit(r, u, m, n, s, v);
				t += p;
			}

			q += o;
		}
	}

	public boolean method_2329(int i, int j, int k, int l) {
		if (!this.display.isHidden() || this.field_2714 != null && this.field_2714.isDone()) {
			int m = i + this.xPos;
			int n = m + 26;
			int o = j + this.yPos;
			int p = o + 26;
			return k >= m && k <= n && l >= o && l <= p;
		} else {
			return false;
		}
	}

	public void method_2332() {
		if (this.field_2706 == null && this.advancement.getParent() != null) {
			this.field_2706 = this.getRootWidget(this.advancement);
			if (this.field_2706 != null) {
				this.field_2706.method_2322(this);
			}
		}
	}

	public int method_2326() {
		return this.yPos;
	}

	public int method_2327() {
		return this.xPos;
	}
}
