package net.minecraft.client.gui.widget;

import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.CachedMapper;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class MultilineTextWidget extends AbstractTextWidget {
	private OptionalInt maxWidth = OptionalInt.empty();
	private OptionalInt maxRows = OptionalInt.empty();
	private final CachedMapper<MultilineTextWidget.CacheKey, MultilineText> cacheKeyToText;
	private boolean centered = false;

	public MultilineTextWidget(Text message, TextRenderer textRenderer) {
		this(0, 0, message, textRenderer);
	}

	public MultilineTextWidget(int x, int y, Text message, TextRenderer textRenderer) {
		super(x, y, 0, 0, message, textRenderer);
		this.cacheKeyToText = Util.cachedMapper(
			cacheKey -> cacheKey.maxRows.isPresent()
					? MultilineText.create(textRenderer, cacheKey.maxWidth, cacheKey.maxRows.getAsInt(), cacheKey.message)
					: MultilineText.create(textRenderer, cacheKey.message, cacheKey.maxWidth)
		);
		this.active = false;
	}

	public MultilineTextWidget setTextColor(int i) {
		super.setTextColor(i);
		return this;
	}

	public MultilineTextWidget setMaxWidth(int maxWidth) {
		this.maxWidth = OptionalInt.of(maxWidth);
		return this;
	}

	public MultilineTextWidget setMaxRows(int maxRows) {
		this.maxRows = OptionalInt.of(maxRows);
		return this;
	}

	public MultilineTextWidget setCentered(boolean centered) {
		this.centered = centered;
		return this;
	}

	@Override
	public int getWidth() {
		return this.cacheKeyToText.map(this.getCacheKey()).getMaxWidth();
	}

	@Override
	public int getHeight() {
		return this.cacheKeyToText.map(this.getCacheKey()).count() * 9;
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		MultilineText multilineText = this.cacheKeyToText.map(this.getCacheKey());
		int i = this.getX();
		int j = this.getY();
		int k = 9;
		int l = this.getTextColor();
		if (this.centered) {
			multilineText.drawCenterWithShadow(context, i + this.getWidth() / 2, j, k, l);
		} else {
			multilineText.drawWithShadow(context, i, j, k, l);
		}
	}

	private MultilineTextWidget.CacheKey getCacheKey() {
		return new MultilineTextWidget.CacheKey(this.getMessage(), this.maxWidth.orElse(Integer.MAX_VALUE), this.maxRows);
	}

	@Environment(EnvType.CLIENT)
	static record CacheKey(Text message, int maxWidth, OptionalInt maxRows) {
	}
}
