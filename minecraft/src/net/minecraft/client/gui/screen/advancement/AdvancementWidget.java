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
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class AdvancementWidget extends DrawableHelper {
	private static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/advancements/widgets.png");
	private static final int field_32286 = 26;
	private static final int field_32287 = 0;
	private static final int field_32288 = 200;
	private static final int field_32289 = 26;
	private static final int field_32290 = 8;
	private static final int field_32291 = 5;
	private static final int field_32292 = 26;
	private static final int field_32293 = 3;
	private static final int field_32294 = 5;
	private static final int field_32295 = 32;
	private static final int field_32296 = 9;
	private static final int field_32297 = 163;
	private static final int[] SPLIT_OFFSET_CANDIDATES = new int[]{0, 10, -10, 25, -25};
	private final AdvancementTab tab;
	private final Advancement advancement;
	private final AdvancementDisplay display;
	private final OrderedText title;
	private final int width;
	private final List<OrderedText> description;
	private final MinecraftClient client;
	@Nullable
	private AdvancementWidget parent;
	private final List<AdvancementWidget> children = Lists.<AdvancementWidget>newArrayList();
	@Nullable
	private AdvancementProgress progress;
	private final int x;
	private final int y;

	public AdvancementWidget(AdvancementTab tab, MinecraftClient client, Advancement advancement, AdvancementDisplay display) {
		this.tab = tab;
		this.advancement = advancement;
		this.display = display;
		this.client = client;
		this.title = Language.getInstance().reorder(client.textRenderer.trimToWidth(display.getTitle(), 163));
		this.x = MathHelper.floor(display.getX() * 28.0F);
		this.y = MathHelper.floor(display.getY() * 27.0F);
		int i = advancement.getRequirementCount();
		int j = String.valueOf(i).length();
		int k = i > 1 ? client.textRenderer.getWidth("  ") + client.textRenderer.getWidth("0") * j * 2 + client.textRenderer.getWidth("/") : 0;
		int l = 29 + client.textRenderer.getWidth(this.title) + k;
		this.description = Language.getInstance()
			.reorder(this.wrapDescription(Texts.setStyleIfAbsent(display.getDescription().shallowCopy(), Style.EMPTY.withColor(display.getFrame().getTitleFormat())), l));

		for (OrderedText orderedText : this.description) {
			l = Math.max(l, client.textRenderer.getWidth(orderedText));
		}

		this.width = l + 3 + 5;
	}

	private static float getMaxWidth(TextHandler textHandler, List<StringVisitable> lines) {
		return (float)lines.stream().mapToDouble(textHandler::getWidth).max().orElse(0.0);
	}

	private List<StringVisitable> wrapDescription(Text text, int width) {
		TextHandler textHandler = this.client.textRenderer.getTextHandler();
		List<StringVisitable> list = null;
		float f = Float.MAX_VALUE;

		for (int i : SPLIT_OFFSET_CANDIDATES) {
			List<StringVisitable> list2 = textHandler.wrapLines(text, width - i, Style.EMPTY);
			float g = Math.abs(getMaxWidth(textHandler, list2) - (float)width);
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

	public void renderLines(MatrixStack matrices, int x, int y, boolean bl) {
		if (this.parent != null) {
			int i = x + this.parent.x + 13;
			int j = x + this.parent.x + 26 + 4;
			int k = y + this.parent.y + 13;
			int l = x + this.x + 13;
			int m = y + this.y + 13;
			int n = bl ? -16777216 : -1;
			if (bl) {
				this.drawHorizontalLine(matrices, j, i, k - 1, n);
				this.drawHorizontalLine(matrices, j + 1, i, k, n);
				this.drawHorizontalLine(matrices, j, i, k + 1, n);
				this.drawHorizontalLine(matrices, l, j - 1, m - 1, n);
				this.drawHorizontalLine(matrices, l, j - 1, m, n);
				this.drawHorizontalLine(matrices, l, j - 1, m + 1, n);
				this.drawVerticalLine(matrices, j - 1, m, k, n);
				this.drawVerticalLine(matrices, j + 1, m, k, n);
			} else {
				this.drawHorizontalLine(matrices, j, i, k, n);
				this.drawHorizontalLine(matrices, l, j, m, n);
				this.drawVerticalLine(matrices, j, m, k, n);
			}
		}

		for (AdvancementWidget advancementWidget : this.children) {
			advancementWidget.renderLines(matrices, x, y, bl);
		}
	}

	public void renderWidgets(MatrixStack matrices, int x, int y) {
		if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
			float f = this.progress == null ? 0.0F : this.progress.getProgressBarPercentage();
			AdvancementObtainedStatus advancementObtainedStatus;
			if (f >= 1.0F) {
				advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
			} else {
				advancementObtainedStatus = AdvancementObtainedStatus.UNOBTAINED;
			}

			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
			this.drawTexture(matrices, x + this.x + 3, y + this.y, this.display.getFrame().getTextureV(), 128 + advancementObtainedStatus.getSpriteIndex() * 26, 26, 26);
			this.client.getItemRenderer().renderInGui(this.display.getIcon(), x + this.x + 8, y + this.y + 5);
		}

		for (AdvancementWidget advancementWidget : this.children) {
			advancementWidget.renderWidgets(matrices, x, y);
		}
	}

	public int getWidth() {
		return this.width;
	}

	public void setProgress(AdvancementProgress progress) {
		this.progress = progress;
	}

	public void addChild(AdvancementWidget widget) {
		this.children.add(widget);
	}

	public void drawTooltip(MatrixStack matrices, int originX, int originY, float alpha, int x, int y) {
		boolean bl = x + originX + this.x + this.width + 26 >= this.tab.getScreen().width;
		String string = this.progress == null ? null : this.progress.getProgressBarFraction();
		int i = string == null ? 0 : this.client.textRenderer.getWidth(string);
		boolean bl2 = 113 - originY - this.y - 26 <= 6 + this.description.size() * 9;
		float f = this.progress == null ? 0.0F : this.progress.getProgressBarPercentage();
		int j = MathHelper.floor(f * (float)this.width);
		AdvancementObtainedStatus advancementObtainedStatus;
		AdvancementObtainedStatus advancementObtainedStatus2;
		AdvancementObtainedStatus advancementObtainedStatus3;
		if (f >= 1.0F) {
			j = this.width / 2;
			advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
			advancementObtainedStatus2 = AdvancementObtainedStatus.OBTAINED;
			advancementObtainedStatus3 = AdvancementObtainedStatus.OBTAINED;
		} else if (j < 2) {
			j = this.width / 2;
			advancementObtainedStatus = AdvancementObtainedStatus.UNOBTAINED;
			advancementObtainedStatus2 = AdvancementObtainedStatus.UNOBTAINED;
			advancementObtainedStatus3 = AdvancementObtainedStatus.UNOBTAINED;
		} else if (j > this.width - 2) {
			j = this.width / 2;
			advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
			advancementObtainedStatus2 = AdvancementObtainedStatus.OBTAINED;
			advancementObtainedStatus3 = AdvancementObtainedStatus.UNOBTAINED;
		} else {
			advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
			advancementObtainedStatus2 = AdvancementObtainedStatus.UNOBTAINED;
			advancementObtainedStatus3 = AdvancementObtainedStatus.UNOBTAINED;
		}

		int k = this.width - j;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableBlend();
		int l = originY + this.y;
		int m;
		if (bl) {
			m = originX + this.x - this.width + 26 + 6;
		} else {
			m = originX + this.x;
		}

		int n = 32 + this.description.size() * 9;
		if (!this.description.isEmpty()) {
			if (bl2) {
				this.method_2324(matrices, m, l + 26 - n, this.width, n, 10, 200, 26, 0, 52);
			} else {
				this.method_2324(matrices, m, l, this.width, n, 10, 200, 26, 0, 52);
			}
		}

		this.drawTexture(matrices, m, l, 0, advancementObtainedStatus.getSpriteIndex() * 26, j, 26);
		this.drawTexture(matrices, m + j, l, 200 - k, advancementObtainedStatus2.getSpriteIndex() * 26, k, 26);
		this.drawTexture(
			matrices, originX + this.x + 3, originY + this.y, this.display.getFrame().getTextureV(), 128 + advancementObtainedStatus3.getSpriteIndex() * 26, 26, 26
		);
		if (bl) {
			this.client.textRenderer.drawWithShadow(matrices, this.title, (float)(m + 5), (float)(originY + this.y + 9), -1);
			if (string != null) {
				this.client.textRenderer.drawWithShadow(matrices, string, (float)(originX + this.x - i), (float)(originY + this.y + 9), -1);
			}
		} else {
			this.client.textRenderer.drawWithShadow(matrices, this.title, (float)(originX + this.x + 32), (float)(originY + this.y + 9), -1);
			if (string != null) {
				this.client.textRenderer.drawWithShadow(matrices, string, (float)(originX + this.x + this.width - i - 5), (float)(originY + this.y + 9), -1);
			}
		}

		if (bl2) {
			for (int o = 0; o < this.description.size(); o++) {
				this.client.textRenderer.draw(matrices, (OrderedText)this.description.get(o), (float)(m + 5), (float)(l + 26 - n + 7 + o * 9), -5592406);
			}
		} else {
			for (int o = 0; o < this.description.size(); o++) {
				this.client.textRenderer.draw(matrices, (OrderedText)this.description.get(o), (float)(m + 5), (float)(originY + this.y + 9 + 17 + o * 9), -5592406);
			}
		}

		this.client.getItemRenderer().renderInGui(this.display.getIcon(), originX + this.x + 8, originY + this.y + 5);
	}

	protected void method_2324(MatrixStack matrices, int x, int y, int i, int j, int k, int l, int m, int n, int o) {
		this.drawTexture(matrices, x, y, n, o, k, k);
		this.method_2321(matrices, x + k, y, i - k - k, k, n + k, o, l - k - k, m);
		this.drawTexture(matrices, x + i - k, y, n + l - k, o, k, k);
		this.drawTexture(matrices, x, y + j - k, n, o + m - k, k, k);
		this.method_2321(matrices, x + k, y + j - k, i - k - k, k, n + k, o + m - k, l - k - k, m);
		this.drawTexture(matrices, x + i - k, y + j - k, n + l - k, o + m - k, k, k);
		this.method_2321(matrices, x, y + k, k, j - k - k, n, o + k, l, m - k - k);
		this.method_2321(matrices, x + k, y + k, i - k - k, j - k - k, n + k, o + k, l - k - k, m - k - k);
		this.method_2321(matrices, x + i - k, y + k, k, j - k - k, n + l - k, o + k, l, m - k - k);
	}

	protected void method_2321(MatrixStack matrices, int x, int y, int i, int j, int k, int l, int m, int n) {
		int o = 0;

		while (o < i) {
			int p = x + o;
			int q = Math.min(m, i - o);
			int r = 0;

			while (r < j) {
				int s = y + r;
				int t = Math.min(n, j - r);
				this.drawTexture(matrices, p, s, k, l, q, t);
				r += n;
			}

			o += m;
		}
	}

	public boolean shouldRender(int originX, int originY, int mouseX, int mouseY) {
		if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
			int i = originX + this.x;
			int j = i + 26;
			int k = originY + this.y;
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
		return this.y;
	}

	public int getX() {
		return this.x;
	}
}
