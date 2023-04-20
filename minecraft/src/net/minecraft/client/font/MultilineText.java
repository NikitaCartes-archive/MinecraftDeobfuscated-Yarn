package net.minecraft.client.font;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public interface MultilineText {
	MultilineText EMPTY = new MultilineText() {
		@Override
		public int drawCenterWithShadow(DrawContext context, int x, int y) {
			return y;
		}

		@Override
		public int drawCenterWithShadow(DrawContext context, int x, int y, int lineHeight, int color) {
			return y;
		}

		@Override
		public int drawWithShadow(DrawContext context, int x, int y, int lineHeight, int color) {
			return y;
		}

		@Override
		public int draw(DrawContext context, int x, int y, int lineHeight, int color) {
			return y;
		}

		@Override
		public void fillBackground(DrawContext context, int centerX, int centerY, int lineHeight, int padding, int color) {
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

	static MultilineText create(TextRenderer renderer, StringVisitable text, int width) {
		return create(
			renderer,
			(List<MultilineText.Line>)renderer.wrapLines(text, width)
				.stream()
				.map(textx -> new MultilineText.Line(textx, renderer.getWidth(textx)))
				.collect(ImmutableList.toImmutableList())
		);
	}

	static MultilineText create(TextRenderer renderer, StringVisitable text, int width, int maxLines) {
		return create(
			renderer,
			(List<MultilineText.Line>)renderer.wrapLines(text, width)
				.stream()
				.limit((long)maxLines)
				.map(textx -> new MultilineText.Line(textx, renderer.getWidth(textx)))
				.collect(ImmutableList.toImmutableList())
		);
	}

	static MultilineText create(TextRenderer renderer, Text... texts) {
		return create(
			renderer,
			(List<MultilineText.Line>)Arrays.stream(texts)
				.map(Text::asOrderedText)
				.map(text -> new MultilineText.Line(text, renderer.getWidth(text)))
				.collect(ImmutableList.toImmutableList())
		);
	}

	static MultilineText createFromTexts(TextRenderer renderer, List<Text> texts) {
		return create(
			renderer,
			(List<MultilineText.Line>)texts.stream()
				.map(Text::asOrderedText)
				.map(text -> new MultilineText.Line(text, renderer.getWidth(text)))
				.collect(ImmutableList.toImmutableList())
		);
	}

	static MultilineText create(TextRenderer textRenderer, List<MultilineText.Line> lines) {
		return lines.isEmpty() ? EMPTY : new MultilineText() {
			private final int maxWidth = lines.stream().mapToInt(line -> line.width).max().orElse(0);

			@Override
			public int drawCenterWithShadow(DrawContext context, int x, int y) {
				return this.drawCenterWithShadow(context, x, y, 9, 16777215);
			}

			@Override
			public int drawCenterWithShadow(DrawContext context, int x, int y, int lineHeight, int color) {
				int i = y;

				for (MultilineText.Line line : lines) {
					context.drawTextWithShadow(textRenderer, line.text, x - line.width / 2, i, color);
					i += lineHeight;
				}

				return i;
			}

			@Override
			public int drawWithShadow(DrawContext context, int x, int y, int lineHeight, int color) {
				int i = y;

				for (MultilineText.Line line : lines) {
					context.drawTextWithShadow(textRenderer, line.text, x, i, color);
					i += lineHeight;
				}

				return i;
			}

			@Override
			public int draw(DrawContext context, int x, int y, int lineHeight, int color) {
				int i = y;

				for (MultilineText.Line line : lines) {
					context.drawText(textRenderer, line.text, x, i, color, false);
					i += lineHeight;
				}

				return i;
			}

			@Override
			public void fillBackground(DrawContext context, int centerX, int centerY, int lineHeight, int padding, int color) {
				int i = lines.stream().mapToInt(line -> line.width).max().orElse(0);
				if (i > 0) {
					context.fill(centerX - i / 2 - padding, centerY - padding, centerX + i / 2 + padding, centerY + lines.size() * lineHeight + padding, color);
				}
			}

			@Override
			public int count() {
				return lines.size();
			}

			@Override
			public int getMaxWidth() {
				return this.maxWidth;
			}
		};
	}

	int drawCenterWithShadow(DrawContext context, int x, int y);

	int drawCenterWithShadow(DrawContext context, int x, int y, int lineHeight, int color);

	int drawWithShadow(DrawContext context, int x, int y, int lineHeight, int color);

	int draw(DrawContext context, int x, int y, int lineHeight, int color);

	void fillBackground(DrawContext context, int centerX, int centerY, int lineHeight, int padding, int color);

	int count();

	int getMaxWidth();

	@Environment(EnvType.CLIENT)
	public static class Line {
		final OrderedText text;
		final int width;

		Line(OrderedText text, int width) {
			this.text = text;
			this.width = width;
		}
	}
}
