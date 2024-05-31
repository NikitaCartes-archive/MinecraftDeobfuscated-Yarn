package net.minecraft.client.font;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Language;

@Environment(EnvType.CLIENT)
public interface MultilineText {
	MultilineText EMPTY = new MultilineText() {
		@Override
		public void drawCenterWithShadow(DrawContext context, int x, int y) {
		}

		@Override
		public void drawCenterWithShadow(DrawContext context, int x, int y, int lineHeight, int color) {
		}

		@Override
		public void drawWithShadow(DrawContext context, int x, int y, int lineHeight, int color) {
		}

		@Override
		public int draw(DrawContext context, int x, int y, int lineHeight, int color) {
			return y;
		}

		@Override
		public int count() {
			return 0;
		}

		@Override
		public int getMaxWidth() {
			return 0;
		}
	};

	static MultilineText create(TextRenderer renderer, Text... texts) {
		return create(renderer, Integer.MAX_VALUE, Integer.MAX_VALUE, texts);
	}

	static MultilineText create(TextRenderer renderer, int maxWidth, Text... texts) {
		return create(renderer, maxWidth, Integer.MAX_VALUE, texts);
	}

	static MultilineText create(TextRenderer renderer, Text text, int maxWidth) {
		return create(renderer, maxWidth, Integer.MAX_VALUE, text);
	}

	static MultilineText create(TextRenderer renderer, int maxWidth, int maxLines, Text... texts) {
		return texts.length == 0 ? EMPTY : new MultilineText() {
			@Nullable
			private List<MultilineText.Line> lines;
			@Nullable
			private Language language;

			@Override
			public void drawCenterWithShadow(DrawContext context, int x, int y) {
				this.drawCenterWithShadow(context, x, y, 9, -1);
			}

			@Override
			public void drawCenterWithShadow(DrawContext context, int x, int y, int lineHeight, int color) {
				int i = y;

				for (MultilineText.Line line : this.getLines()) {
					context.drawCenteredTextWithShadow(renderer, line.text, x, i, color);
					i += lineHeight;
				}
			}

			@Override
			public void drawWithShadow(DrawContext context, int x, int y, int lineHeight, int color) {
				int i = y;

				for (MultilineText.Line line : this.getLines()) {
					context.drawTextWithShadow(renderer, line.text, x, i, color);
					i += lineHeight;
				}
			}

			@Override
			public int draw(DrawContext context, int x, int y, int lineHeight, int color) {
				int i = y;

				for (MultilineText.Line line : this.getLines()) {
					context.drawText(renderer, line.text, x, i, color, false);
					i += lineHeight;
				}

				return i;
			}

			private List<MultilineText.Line> getLines() {
				Language language = Language.getInstance();
				if (this.lines != null && language == this.language) {
					return this.lines;
				} else {
					this.language = language;
					List<OrderedText> list = new ArrayList();

					for (Text text : texts) {
						list.addAll(renderer.wrapLines(text, maxWidth));
					}

					this.lines = new ArrayList();

					for (OrderedText orderedText : list.subList(0, Math.min(list.size(), maxLines))) {
						this.lines.add(new MultilineText.Line(orderedText, renderer.getWidth(orderedText)));
					}

					return this.lines;
				}
			}

			@Override
			public int count() {
				return this.getLines().size();
			}

			@Override
			public int getMaxWidth() {
				return Math.min(maxWidth, this.getLines().stream().mapToInt(MultilineText.Line::width).max().orElse(0));
			}
		};
	}

	void drawCenterWithShadow(DrawContext context, int x, int y);

	void drawCenterWithShadow(DrawContext context, int x, int y, int lineHeight, int color);

	void drawWithShadow(DrawContext context, int x, int y, int lineHeight, int color);

	int draw(DrawContext context, int x, int y, int lineHeight, int color);

	int count();

	int getMaxWidth();

	@Environment(EnvType.CLIENT)
	public static record Line(OrderedText text, int width) {
	}
}
