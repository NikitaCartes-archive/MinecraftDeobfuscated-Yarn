package net.minecraft.client.gui.screen.advancement;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class AdvancementWidget {
	private static final Identifier TITLE_BOX_TEXTURE = new Identifier("advancements/title_box");
	private static final int field_32286 = 26;
	private static final int field_32287 = 0;
	private static final int field_32288 = 200;
	private static final int field_32289 = 26;
	private static final int ICON_OFFSET_X = 8;
	private static final int ICON_OFFSET_Y = 5;
	private static final int ICON_SIZE = 26;
	private static final int field_32293 = 3;
	private static final int field_32294 = 5;
	private static final int TITLE_OFFSET_X = 32;
	private static final int TITLE_OFFSET_Y = 9;
	private static final int TITLE_MAX_WIDTH = 163;
	private static final int[] SPLIT_OFFSET_CANDIDATES = new int[]{0, 10, -10, 25, -25};
	private final AdvancementTab tab;
	private final PlacedAdvancement advancement;
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

	public AdvancementWidget(AdvancementTab tab, MinecraftClient client, PlacedAdvancement advancement, AdvancementDisplay display) {
		this.tab = tab;
		this.advancement = advancement;
		this.display = display;
		this.client = client;
		this.title = Language.getInstance().reorder(client.textRenderer.trimToWidth(display.getTitle(), 163));
		this.x = MathHelper.floor(display.getX() * 28.0F);
		this.y = MathHelper.floor(display.getY() * 27.0F);
		int i = advancement.getAdvancement().requirements().getLength();
		int j = String.valueOf(i).length();
		int k = i > 1 ? client.textRenderer.getWidth("  ") + client.textRenderer.getWidth("0") * j * 2 + client.textRenderer.getWidth("/") : 0;
		int l = 29 + client.textRenderer.getWidth(this.title) + k;
		this.description = Language.getInstance()
			.reorder(this.wrapDescription(Texts.setStyleIfAbsent(display.getDescription().copy(), Style.EMPTY.withColor(display.getFrame().getTitleFormat())), l));

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
	private AdvancementWidget getParent(PlacedAdvancement advancement) {
		do {
			advancement = advancement.getParent();
		} while (advancement != null && advancement.getAdvancement().display().isEmpty());

		return advancement != null && !advancement.getAdvancement().display().isEmpty() ? this.tab.getWidget(advancement.getAdvancementEntry()) : null;
	}

	public void renderLines(DrawContext context, int x, int y, boolean border) {
		if (this.parent != null) {
			int i = x + this.parent.x + 13;
			int j = x + this.parent.x + 26 + 4;
			int k = y + this.parent.y + 13;
			int l = x + this.x + 13;
			int m = y + this.y + 13;
			int n = border ? -16777216 : -1;
			if (border) {
				context.drawHorizontalLine(j, i, k - 1, n);
				context.drawHorizontalLine(j + 1, i, k, n);
				context.drawHorizontalLine(j, i, k + 1, n);
				context.drawHorizontalLine(l, j - 1, m - 1, n);
				context.drawHorizontalLine(l, j - 1, m, n);
				context.drawHorizontalLine(l, j - 1, m + 1, n);
				context.drawVerticalLine(j - 1, m, k, n);
				context.drawVerticalLine(j + 1, m, k, n);
			} else {
				context.drawHorizontalLine(j, i, k, n);
				context.drawHorizontalLine(l, j, m, n);
				context.drawVerticalLine(j, m, k, n);
			}
		}

		for (AdvancementWidget advancementWidget : this.children) {
			advancementWidget.renderLines(context, x, y, border);
		}
	}

	public void renderWidgets(DrawContext context, int x, int y) {
		if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
			float f = this.progress == null ? 0.0F : this.progress.getProgressBarPercentage();
			AdvancementObtainedStatus advancementObtainedStatus;
			if (f >= 1.0F) {
				advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
			} else {
				advancementObtainedStatus = AdvancementObtainedStatus.UNOBTAINED;
			}

			context.drawGuiTexture(advancementObtainedStatus.getFrameTexture(this.display.getFrame()), x + this.x + 3, y + this.y, 26, 26);
			context.drawItemWithoutEntity(this.display.getIcon(), x + this.x + 8, y + this.y + 5);
		}

		for (AdvancementWidget advancementWidget : this.children) {
			advancementWidget.renderWidgets(context, x, y);
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

	public void drawTooltip(DrawContext context, int originX, int originY, float alpha, int x, int y) {
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
				context.drawGuiTexture(TITLE_BOX_TEXTURE, m, l + 26 - n, this.width, n);
			} else {
				context.drawGuiTexture(TITLE_BOX_TEXTURE, m, l, this.width, n);
			}
		}

		context.drawGuiTexture(advancementObtainedStatus.getBoxTexture(), 200, 26, 0, 0, m, l, j, 26);
		context.drawGuiTexture(advancementObtainedStatus2.getBoxTexture(), 200, 26, 200 - k, 0, m + j, l, k, 26);
		context.drawGuiTexture(advancementObtainedStatus3.getFrameTexture(this.display.getFrame()), originX + this.x + 3, originY + this.y, 26, 26);
		if (bl) {
			context.drawTextWithShadow(this.client.textRenderer, this.title, m + 5, originY + this.y + 9, -1);
			if (string != null) {
				context.drawTextWithShadow(this.client.textRenderer, string, originX + this.x - i, originY + this.y + 9, -1);
			}
		} else {
			context.drawTextWithShadow(this.client.textRenderer, this.title, originX + this.x + 32, originY + this.y + 9, -1);
			if (string != null) {
				context.drawTextWithShadow(this.client.textRenderer, string, originX + this.x + this.width - i - 5, originY + this.y + 9, -1);
			}
		}

		if (bl2) {
			for (int o = 0; o < this.description.size(); o++) {
				context.drawText(this.client.textRenderer, (OrderedText)this.description.get(o), m + 5, l + 26 - n + 7 + o * 9, -5592406, false);
			}
		} else {
			for (int o = 0; o < this.description.size(); o++) {
				context.drawText(this.client.textRenderer, (OrderedText)this.description.get(o), m + 5, originY + this.y + 9 + 17 + o * 9, -5592406, false);
			}
		}

		context.drawItemWithoutEntity(this.display.getIcon(), originX + this.x + 8, originY + this.y + 5);
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
