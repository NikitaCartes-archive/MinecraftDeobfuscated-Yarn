package net.minecraft.client.gui.screen.advancement;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class AdvancementWidget extends DrawableHelper {
	private static final Identifier WIDGETS_TEX = new Identifier("textures/gui/advancements/widgets.png");
	private static final int[] field_24262 = new int[]{0, 10, -10, 25, -25};
	private final AdvancementTab tab;
	private final Advancement advancement;
	private final AdvancementDisplay display;
	private final StringRenderable title;
	private final int width;
	private final List<StringRenderable> description;
	private final MinecraftClient client;
	private AdvancementWidget parent;
	private final List<AdvancementWidget> children = Lists.<AdvancementWidget>newArrayList();
	private AdvancementProgress progress;
	private final int xPos;
	private final int yPos;

	public AdvancementWidget(AdvancementTab tab, MinecraftClient client, Advancement advancement, AdvancementDisplay display) {
		this.tab = tab;
		this.advancement = advancement;
		this.display = display;
		this.client = client;
		this.title = client.textRenderer.trimToWidth(display.getTitle(), 163);
		this.xPos = MathHelper.floor(display.getX() * 28.0F);
		this.yPos = MathHelper.floor(display.getY() * 27.0F);
		int i = advancement.getRequirementCount();
		int j = String.valueOf(i).length();
		int k = i > 1 ? client.textRenderer.getWidth("  ") + client.textRenderer.getWidth("0") * j * 2 + client.textRenderer.getWidth("/") : 0;
		int l = 29 + client.textRenderer.getWidth(this.title) + k;
		this.description = this.wrapDescription(display.getDescription().shallowCopy().formatted(display.getFrame().getTitleFormat()), l);

		for (StringRenderable stringRenderable : this.description) {
			l = Math.max(l, client.textRenderer.getWidth(stringRenderable));
		}

		this.width = l + 3 + 5;
	}

	private static float method_27572(TextHandler textHandler, List<StringRenderable> list) {
		return (float)list.stream().mapToDouble(textHandler::getWidth).max().orElse(0.0);
	}

	private List<StringRenderable> wrapDescription(Text text, int width) {
		TextHandler textHandler = this.client.textRenderer.getTextHandler();
		List<StringRenderable> list = null;
		float f = Float.MAX_VALUE;

		for (int i : field_24262) {
			List<StringRenderable> list2 = textHandler.wrapLines(text, width - i, Style.EMPTY);
			float g = Math.abs(method_27572(textHandler, list2) - (float)width);
			if (g <= 10.0F) {
				return list2;
			}

			if (g < f) {
				f = g;
				list = list2;
			}
		}

		return list;
	}

	@Nullable
	private AdvancementWidget getParent(Advancement advancement) {
		do {
			advancement = advancement.getParent();
		} while (advancement != null && advancement.getDisplay() == null);

		return advancement != null && advancement.getDisplay() != null ? this.tab.getWidget(advancement) : null;
	}

	public void renderLines(MatrixStack matrixStack, int i, int j, boolean bl) {
		if (this.parent != null) {
			int k = i + this.parent.xPos + 13;
			int l = i + this.parent.xPos + 26 + 4;
			int m = j + this.parent.yPos + 13;
			int n = i + this.xPos + 13;
			int o = j + this.yPos + 13;
			int p = bl ? -16777216 : -1;
			if (bl) {
				this.drawHorizontalLine(matrixStack, l, k, m - 1, p);
				this.drawHorizontalLine(matrixStack, l + 1, k, m, p);
				this.drawHorizontalLine(matrixStack, l, k, m + 1, p);
				this.drawHorizontalLine(matrixStack, n, l - 1, o - 1, p);
				this.drawHorizontalLine(matrixStack, n, l - 1, o, p);
				this.drawHorizontalLine(matrixStack, n, l - 1, o + 1, p);
				this.drawVerticalLine(matrixStack, l - 1, o, m, p);
				this.drawVerticalLine(matrixStack, l + 1, o, m, p);
			} else {
				this.drawHorizontalLine(matrixStack, l, k, m, p);
				this.drawHorizontalLine(matrixStack, n, l, o, p);
				this.drawVerticalLine(matrixStack, l, o, m, p);
			}
		}

		for (AdvancementWidget advancementWidget : this.children) {
			advancementWidget.renderLines(matrixStack, i, j, bl);
		}
	}

	public void renderWidgets(MatrixStack matrixStack, int i, int j) {
		if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
			float f = this.progress == null ? 0.0F : this.progress.getProgressBarPercentage();
			AdvancementObtainedStatus advancementObtainedStatus;
			if (f >= 1.0F) {
				advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
			} else {
				advancementObtainedStatus = AdvancementObtainedStatus.UNOBTAINED;
			}

			this.client.getTextureManager().bindTexture(WIDGETS_TEX);
			this.drawTexture(
				matrixStack, i + this.xPos + 3, j + this.yPos, this.display.getFrame().texV(), 128 + advancementObtainedStatus.getSpriteIndex() * 26, 26, 26
			);
			this.client.getItemRenderer().renderInGui(this.display.getIcon(), i + this.xPos + 8, j + this.yPos + 5);
		}

		for (AdvancementWidget advancementWidget : this.children) {
			advancementWidget.renderWidgets(matrixStack, i, j);
		}
	}

	public void setProgress(AdvancementProgress progress) {
		this.progress = progress;
	}

	public void addChild(AdvancementWidget widget) {
		this.children.add(widget);
	}

	public void drawTooltip(MatrixStack matrixStack, int i, int j, float f, int y, int k) {
		boolean bl = y + i + this.xPos + this.width + 26 >= this.tab.getScreen().width;
		String string = this.progress == null ? null : this.progress.getProgressBarFraction();
		int l = string == null ? 0 : this.client.textRenderer.getWidth(string);
		boolean bl2 = 113 - j - this.yPos - 26 <= 6 + this.description.size() * 9;
		float g = this.progress == null ? 0.0F : this.progress.getProgressBarPercentage();
		int m = MathHelper.floor(g * (float)this.width);
		AdvancementObtainedStatus advancementObtainedStatus;
		AdvancementObtainedStatus advancementObtainedStatus2;
		AdvancementObtainedStatus advancementObtainedStatus3;
		if (g >= 1.0F) {
			m = this.width / 2;
			advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
			advancementObtainedStatus2 = AdvancementObtainedStatus.OBTAINED;
			advancementObtainedStatus3 = AdvancementObtainedStatus.OBTAINED;
		} else if (m < 2) {
			m = this.width / 2;
			advancementObtainedStatus = AdvancementObtainedStatus.UNOBTAINED;
			advancementObtainedStatus2 = AdvancementObtainedStatus.UNOBTAINED;
			advancementObtainedStatus3 = AdvancementObtainedStatus.UNOBTAINED;
		} else if (m > this.width - 2) {
			m = this.width / 2;
			advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
			advancementObtainedStatus2 = AdvancementObtainedStatus.OBTAINED;
			advancementObtainedStatus3 = AdvancementObtainedStatus.UNOBTAINED;
		} else {
			advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
			advancementObtainedStatus2 = AdvancementObtainedStatus.UNOBTAINED;
			advancementObtainedStatus3 = AdvancementObtainedStatus.UNOBTAINED;
		}

		int n = this.width - m;
		this.client.getTextureManager().bindTexture(WIDGETS_TEX);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableBlend();
		int o = j + this.yPos;
		int p;
		if (bl) {
			p = i + this.xPos - this.width + 26 + 6;
		} else {
			p = i + this.xPos;
		}

		int q = 32 + this.description.size() * 9;
		if (!this.description.isEmpty()) {
			if (bl2) {
				this.method_2324(matrixStack, p, o + 26 - q, this.width, q, 10, 200, 26, 0, 52);
			} else {
				this.method_2324(matrixStack, p, o, this.width, q, 10, 200, 26, 0, 52);
			}
		}

		this.drawTexture(matrixStack, p, o, 0, advancementObtainedStatus.getSpriteIndex() * 26, m, 26);
		this.drawTexture(matrixStack, p + m, o, 200 - n, advancementObtainedStatus2.getSpriteIndex() * 26, n, 26);
		this.drawTexture(
			matrixStack, i + this.xPos + 3, j + this.yPos, this.display.getFrame().texV(), 128 + advancementObtainedStatus3.getSpriteIndex() * 26, 26, 26
		);
		if (bl) {
			this.client.textRenderer.drawWithShadow(matrixStack, this.title, (float)(p + 5), (float)(j + this.yPos + 9), -1);
			if (string != null) {
				this.client.textRenderer.drawWithShadow(matrixStack, string, (float)(i + this.xPos - l), (float)(j + this.yPos + 9), -1);
			}
		} else {
			this.client.textRenderer.drawWithShadow(matrixStack, this.title, (float)(i + this.xPos + 32), (float)(j + this.yPos + 9), -1);
			if (string != null) {
				this.client.textRenderer.drawWithShadow(matrixStack, string, (float)(i + this.xPos + this.width - l - 5), (float)(j + this.yPos + 9), -1);
			}
		}

		if (bl2) {
			for (int r = 0; r < this.description.size(); r++) {
				this.client.textRenderer.draw(matrixStack, (StringRenderable)this.description.get(r), (float)(p + 5), (float)(o + 26 - q + 7 + r * 9), -5592406);
			}
		} else {
			for (int r = 0; r < this.description.size(); r++) {
				this.client.textRenderer.draw(matrixStack, (StringRenderable)this.description.get(r), (float)(p + 5), (float)(j + this.yPos + 9 + 17 + r * 9), -5592406);
			}
		}

		this.client.getItemRenderer().renderInGui(this.display.getIcon(), i + this.xPos + 8, j + this.yPos + 5);
	}

	protected void method_2324(MatrixStack matrixStack, int i, int j, int k, int l, int m, int n, int o, int p, int q) {
		this.drawTexture(matrixStack, i, j, p, q, m, m);
		this.method_2321(matrixStack, i + m, j, k - m - m, m, p + m, q, n - m - m, o);
		this.drawTexture(matrixStack, i + k - m, j, p + n - m, q, m, m);
		this.drawTexture(matrixStack, i, j + l - m, p, q + o - m, m, m);
		this.method_2321(matrixStack, i + m, j + l - m, k - m - m, m, p + m, q + o - m, n - m - m, o);
		this.drawTexture(matrixStack, i + k - m, j + l - m, p + n - m, q + o - m, m, m);
		this.method_2321(matrixStack, i, j + m, m, l - m - m, p, q + m, n, o - m - m);
		this.method_2321(matrixStack, i + m, j + m, k - m - m, l - m - m, p + m, q + m, n - m - m, o - m - m);
		this.method_2321(matrixStack, i + k - m, j + m, m, l - m - m, p + n - m, q + m, n, o - m - m);
	}

	protected void method_2321(MatrixStack matrixStack, int i, int j, int k, int l, int m, int n, int o, int p) {
		int q = 0;

		while (q < k) {
			int r = i + q;
			int s = Math.min(o, k - q);
			int t = 0;

			while (t < l) {
				int u = j + t;
				int v = Math.min(p, l - t);
				this.drawTexture(matrixStack, r, u, m, n, s, v);
				t += p;
			}

			q += o;
		}
	}

	public boolean shouldRender(int originX, int originY, int mouseX, int mouseY) {
		if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
			int i = originX + this.xPos;
			int j = i + 26;
			int k = originY + this.yPos;
			int l = k + 26;
			return mouseX >= i && mouseX <= j && mouseY >= k && mouseY <= l;
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
