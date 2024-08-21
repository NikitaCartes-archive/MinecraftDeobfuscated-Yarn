package net.minecraft.client.gui.tooltip;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.math.Fraction;

@Environment(EnvType.CLIENT)
public class BundleTooltipComponent implements TooltipComponent {
	private static final Identifier BUNDLE_PROGRESSBAR_BORDER_TEXTURE = Identifier.ofVanilla("container/bundle/bundle_progressbar_border");
	private static final int SLOTS_PER_ROW = 4;
	private static final int SLOT_DIMENSION = 24;
	private static final int ROW_WIDTH = 96;
	private static final int field_52816 = 13;
	private static final int field_52817 = 96;
	private static final int field_52818 = 1;
	private static final int PROGRESS_BAR_WIDTH = 94;
	private static final int field_52820 = 4;
	private static final Text BUNDLE_FULL = Text.translatable("item.minecraft.bundle.full");
	private static final Text BUNDLE_EMPTY = Text.translatable("item.minecraft.bundle.empty");
	private static final Text BUNDLE_EMPTY_DESCRIPTION = Text.translatable("item.minecraft.bundle.empty.description");
	private final BundleContentsComponent bundleContents;

	public BundleTooltipComponent(BundleContentsComponent bundleContents) {
		this.bundleContents = bundleContents;
	}

	@Override
	public int getHeight(TextRenderer textRenderer) {
		return this.bundleContents.isEmpty() ? getHeightOfEmpty(textRenderer) : this.getHeightOfNonEmpty();
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return 96;
	}

	@Override
	public boolean isSticky() {
		return true;
	}

	private static int getHeightOfEmpty(TextRenderer textRenderer) {
		return getDescriptionHeight(textRenderer) + 13 + 8;
	}

	private int getHeightOfNonEmpty() {
		return this.getRowsHeight() + 13 + 8;
	}

	private int getRowsHeight() {
		return this.getRows() * 24;
	}

	private int getRows() {
		return MathHelper.ceilDiv(this.getNumVisibleSlots(), 4);
	}

	private int getNumVisibleSlots() {
		return Math.min(12, this.bundleContents.size());
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
		if (this.bundleContents.isEmpty()) {
			this.drawEmptyTooltip(textRenderer, x, y, context);
		} else {
			this.drawNonEmptyTooltip(textRenderer, x, y, context);
		}
	}

	private void drawEmptyTooltip(TextRenderer textRenderer, int x, int y, DrawContext drawContext) {
		drawEmptyDescription(x, y, textRenderer, drawContext);
		this.drawProgressBar(x, y + getDescriptionHeight(textRenderer) + 4, textRenderer, drawContext);
	}

	private void drawNonEmptyTooltip(TextRenderer textRenderer, int x, int y, DrawContext drawContext) {
		boolean bl = this.bundleContents.size() > 12;
		List<ItemStack> list = this.firstStacksInContents(this.bundleContents.getNumberOfStacksShown());
		int i = x + 96;
		int j = y + this.getRows() * 24;
		int k = 1;

		for (int l = 1; l <= this.getRows(); l++) {
			for (int m = 1; m <= 4; m++) {
				int n = i - m * 24;
				int o = j - l * 24;
				if (shouldDrawExtraItemsCount(bl, m, l)) {
					drawExtraItemsCount(n, o, this.numContentItemsAfter(list), textRenderer, drawContext);
				} else if (shouldDrawItem(list, k)) {
					this.drawItem(k, n, o, list, k, textRenderer, drawContext);
					k++;
				}
			}
		}

		this.drawSelectedItemTooltip(textRenderer, drawContext, x, y);
		this.drawProgressBar(x, y + this.getRowsHeight() + 4, textRenderer, drawContext);
	}

	private List<ItemStack> firstStacksInContents(int numberOfStacksShown) {
		int i = Math.min(this.bundleContents.size(), numberOfStacksShown);
		return this.bundleContents.stream().toList().subList(0, i);
	}

	private static boolean shouldDrawExtraItemsCount(boolean hasMoreItems, int column, int row) {
		return hasMoreItems && column * row == 1;
	}

	private static boolean shouldDrawItem(List<ItemStack> items, int itemIndex) {
		return items.size() >= itemIndex;
	}

	private int numContentItemsAfter(List<ItemStack> items) {
		return this.bundleContents.stream().skip((long)items.size()).mapToInt(ItemStack::getCount).sum();
	}

	private void drawItem(int index, int x, int y, List<ItemStack> items, int seed, TextRenderer textRenderer, DrawContext drawContext) {
		int i = items.size() - index;
		ItemStack itemStack = (ItemStack)items.get(i);
		this.drawBackground(i, drawContext, x, y);
		drawContext.drawItem(itemStack, x + 4, y + 4, seed);
		drawContext.drawItemInSlot(textRenderer, itemStack, x + 4, y + 4);
	}

	private static void drawExtraItemsCount(int x, int y, int numExtra, TextRenderer textRenderer, DrawContext drawContext) {
		drawContext.drawCenteredTextWithShadow(textRenderer, "+" + numExtra, x + 12, y + 10, 16777215);
	}

	private void drawBackground(int index, DrawContext drawContext, int x, int y) {
		if (index != -1 && index == this.bundleContents.getSelectedStackIndex()) {
			drawContext.fillGradient(RenderLayer.getGui(), x, y, x + 24, y + 24, -2130706433, -2130706433, 0);
		}
	}

	private void drawSelectedItemTooltip(TextRenderer textRenderer, DrawContext drawContext, int x, int y) {
		if (this.bundleContents.hasSelectedStack()) {
			ItemStack itemStack = this.bundleContents.get(this.bundleContents.getSelectedStackIndex());
			Text text = itemStack.getName();
			int i = textRenderer.getWidth(text.asOrderedText());
			int j = x + this.getWidth(textRenderer) / 2 - 12;
			drawContext.drawTooltip(textRenderer, text, j - i / 2, y - 15);
		}
	}

	private void drawProgressBar(int x, int y, TextRenderer textRenderer, DrawContext drawContext) {
		drawContext.fill(
			RenderLayer.getGui(), x + 1, y, x + 1 + this.getProgressBarFill(), y + 13, BundleItem.getItemBarColor(this.bundleContents.getOccupancy()) | Colors.BLACK
		);
		drawContext.drawGuiTexture(RenderLayer::getGuiTextured, BUNDLE_PROGRESSBAR_BORDER_TEXTURE, x, y, 96, 13);
		Text text = this.getProgressBarLabel();
		if (text != null) {
			drawContext.drawCenteredTextWithShadow(textRenderer, text, x + 48, y + 3, 16777215);
		}
	}

	private static void drawEmptyDescription(int x, int y, TextRenderer textRenderer, DrawContext drawContext) {
		drawContext.drawTextWrapped(textRenderer, BUNDLE_EMPTY_DESCRIPTION, x, y, 96, 11184810);
	}

	private static int getDescriptionHeight(TextRenderer textRenderer) {
		return textRenderer.wrapLines(BUNDLE_EMPTY_DESCRIPTION, 96).size() * 9;
	}

	private int getProgressBarFill() {
		return MathHelper.multiplyFraction(this.bundleContents.getOccupancy(), 94);
	}

	@Nullable
	private Text getProgressBarLabel() {
		if (this.bundleContents.isEmpty()) {
			return BUNDLE_EMPTY;
		} else {
			return this.bundleContents.getOccupancy().compareTo(Fraction.ONE) >= 0 ? BUNDLE_FULL : null;
		}
	}
}
