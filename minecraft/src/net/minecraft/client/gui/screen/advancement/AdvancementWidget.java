package net.minecraft.client.gui.screen.advancement;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class AdvancementWidget extends DrawableHelper {
	private static final Identifier WIDGETS_TEX = new Identifier("textures/gui/advancements/widgets.png");
	private static final Pattern BACKSLASH_S_PATTERN = Pattern.compile("(.+) \\S+");
	private final AdvancementTab tab;
	private final Advancement advancement;
	private final AdvancementDisplay display;
	private final String title;
	private final int width;
	private final List<String> description;
	private final MinecraftClient client;
	private AdvancementWidget parent;
	private final List<AdvancementWidget> children = Lists.<AdvancementWidget>newArrayList();
	private AdvancementProgress progress;
	private final int xPos;
	private final int yPos;

	public AdvancementWidget(AdvancementTab advancementTab, MinecraftClient minecraftClient, Advancement advancement, AdvancementDisplay advancementDisplay) {
		this.tab = advancementTab;
		this.advancement = advancement;
		this.display = advancementDisplay;
		this.client = minecraftClient;
		this.title = minecraftClient.textRenderer.trimToWidth(advancementDisplay.getTitle().asFormattedString(), 163);
		this.xPos = MathHelper.floor(advancementDisplay.getX() * 28.0F);
		this.yPos = MathHelper.floor(advancementDisplay.getY() * 27.0F);
		int i = advancement.getRequirementCount();
		int j = String.valueOf(i).length();
		int k = i > 1
			? minecraftClient.textRenderer.getStringWidth("  ")
				+ minecraftClient.textRenderer.getStringWidth("0") * j * 2
				+ minecraftClient.textRenderer.getStringWidth("/")
			: 0;
		int l = 29 + minecraftClient.textRenderer.getStringWidth(this.title) + k;
		String string = advancementDisplay.getDescription().asFormattedString();
		this.description = this.wrapDescription(string, l);

		for (String string2 : this.description) {
			l = Math.max(l, minecraftClient.textRenderer.getStringWidth(string2));
		}

		this.width = l + 3 + 5;
	}

	private List<String> wrapDescription(String string, int i) {
		if (string.isEmpty()) {
			return Collections.emptyList();
		} else {
			List<String> list = this.client.textRenderer.wrapStringToWidthAsList(string, i);
			if (list.size() < 2) {
				return list;
			} else {
				String string2 = (String)list.get(0);
				String string3 = (String)list.get(1);
				int j = this.client.textRenderer.getStringWidth(string2 + ' ' + string3.split(" ")[0]);
				if (j - i <= 10) {
					return this.client.textRenderer.wrapStringToWidthAsList(string, j);
				} else {
					Matcher matcher = BACKSLASH_S_PATTERN.matcher(string2);
					if (matcher.matches()) {
						int k = this.client.textRenderer.getStringWidth(matcher.group(1));
						if (i - k <= 10) {
							return this.client.textRenderer.wrapStringToWidthAsList(string, k);
						}
					}

					return list;
				}
			}
		}
	}

	@Nullable
	private AdvancementWidget getParent(Advancement advancement) {
		do {
			advancement = advancement.getParent();
		} while (advancement != null && advancement.getDisplay() == null);

		return advancement != null && advancement.getDisplay() != null ? this.tab.getWidget(advancement) : null;
	}

	public void renderLines(int i, int j, boolean bl) {
		if (this.parent != null) {
			int k = i + this.parent.xPos + 13;
			int l = i + this.parent.xPos + 26 + 4;
			int m = j + this.parent.yPos + 13;
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
			advancementWidget.renderLines(i, j, bl);
		}
	}

	public void renderWidgets(int i, int j) {
		if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
			float f = this.progress == null ? 0.0F : this.progress.getProgressBarPercentage();
			AdvancementObtainedStatus advancementObtainedStatus;
			if (f >= 1.0F) {
				advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
			} else {
				advancementObtainedStatus = AdvancementObtainedStatus.UNOBTAINED;
			}

			this.client.getTextureManager().bindTexture(WIDGETS_TEX);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.enableBlend();
			this.blit(i + this.xPos + 3, j + this.yPos, this.display.getFrame().texV(), 128 + advancementObtainedStatus.getSpriteIndex() * 26, 26, 26);
			this.client.getItemRenderer().renderGuiItem(null, this.display.getIcon(), i + this.xPos + 8, j + this.yPos + 5);
		}

		for (AdvancementWidget advancementWidget : this.children) {
			advancementWidget.renderWidgets(i, j);
		}
	}

	public void setProgress(AdvancementProgress advancementProgress) {
		this.progress = advancementProgress;
	}

	public void addChild(AdvancementWidget advancementWidget) {
		this.children.add(advancementWidget);
	}

	public void drawTooltip(int i, int j, float f, int k, int l) {
		boolean bl = k + i + this.xPos + this.width + 26 >= this.tab.getScreen().width;
		String string = this.progress == null ? null : this.progress.getProgressBarFraction();
		int m = string == null ? 0 : this.client.textRenderer.getStringWidth(string);
		boolean bl2 = 113 - j - this.yPos - 26 <= 6 + this.description.size() * 9;
		float g = this.progress == null ? 0.0F : this.progress.getProgressBarPercentage();
		int n = MathHelper.floor(g * (float)this.width);
		AdvancementObtainedStatus advancementObtainedStatus;
		AdvancementObtainedStatus advancementObtainedStatus2;
		AdvancementObtainedStatus advancementObtainedStatus3;
		if (g >= 1.0F) {
			n = this.width / 2;
			advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
			advancementObtainedStatus2 = AdvancementObtainedStatus.OBTAINED;
			advancementObtainedStatus3 = AdvancementObtainedStatus.OBTAINED;
		} else if (n < 2) {
			n = this.width / 2;
			advancementObtainedStatus = AdvancementObtainedStatus.UNOBTAINED;
			advancementObtainedStatus2 = AdvancementObtainedStatus.UNOBTAINED;
			advancementObtainedStatus3 = AdvancementObtainedStatus.UNOBTAINED;
		} else if (n > this.width - 2) {
			n = this.width / 2;
			advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
			advancementObtainedStatus2 = AdvancementObtainedStatus.OBTAINED;
			advancementObtainedStatus3 = AdvancementObtainedStatus.UNOBTAINED;
		} else {
			advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
			advancementObtainedStatus2 = AdvancementObtainedStatus.UNOBTAINED;
			advancementObtainedStatus3 = AdvancementObtainedStatus.UNOBTAINED;
		}

		int o = this.width - n;
		this.client.getTextureManager().bindTexture(WIDGETS_TEX);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableBlend();
		int p = j + this.yPos;
		int q;
		if (bl) {
			q = i + this.xPos - this.width + 26 + 6;
		} else {
			q = i + this.xPos;
		}

		int r = 32 + this.description.size() * 9;
		if (!this.description.isEmpty()) {
			if (bl2) {
				this.method_2324(q, p + 26 - r, this.width, r, 10, 200, 26, 0, 52);
			} else {
				this.method_2324(q, p, this.width, r, 10, 200, 26, 0, 52);
			}
		}

		this.blit(q, p, 0, advancementObtainedStatus.getSpriteIndex() * 26, n, 26);
		this.blit(q + n, p, 200 - o, advancementObtainedStatus2.getSpriteIndex() * 26, o, 26);
		this.blit(i + this.xPos + 3, j + this.yPos, this.display.getFrame().texV(), 128 + advancementObtainedStatus3.getSpriteIndex() * 26, 26, 26);
		if (bl) {
			this.client.textRenderer.drawWithShadow(this.title, (float)(q + 5), (float)(j + this.yPos + 9), -1);
			if (string != null) {
				this.client.textRenderer.drawWithShadow(string, (float)(i + this.xPos - m), (float)(j + this.yPos + 9), -1);
			}
		} else {
			this.client.textRenderer.drawWithShadow(this.title, (float)(i + this.xPos + 32), (float)(j + this.yPos + 9), -1);
			if (string != null) {
				this.client.textRenderer.drawWithShadow(string, (float)(i + this.xPos + this.width - m - 5), (float)(j + this.yPos + 9), -1);
			}
		}

		if (bl2) {
			for (int s = 0; s < this.description.size(); s++) {
				this.client.textRenderer.draw((String)this.description.get(s), (float)(q + 5), (float)(p + 26 - r + 7 + s * 9), -5592406);
			}
		} else {
			for (int s = 0; s < this.description.size(); s++) {
				this.client.textRenderer.draw((String)this.description.get(s), (float)(q + 5), (float)(j + this.yPos + 9 + 17 + s * 9), -5592406);
			}
		}

		this.client.getItemRenderer().renderGuiItem(null, this.display.getIcon(), i + this.xPos + 8, j + this.yPos + 5);
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

	public boolean shouldRender(int i, int j, int k, int l) {
		if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
			int m = i + this.xPos;
			int n = m + 26;
			int o = j + this.yPos;
			int p = o + 26;
			return k >= m && k <= n && l >= o && l <= p;
		} else {
			return false;
		}
	}

	public void addToTree() {
		if (this.parent == null && this.advancement.getParent() != null) {
			this.parent = this.getParent(this.advancement);
			if (this.parent != null) {
				this.parent.addChild(this);
			}
		}
	}

	public int getY() {
		return this.yPos;
	}

	public int getX() {
		return this.xPos;
	}
}
