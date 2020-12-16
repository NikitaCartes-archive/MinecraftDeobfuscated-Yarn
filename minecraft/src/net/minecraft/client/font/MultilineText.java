package net.minecraft.client.font;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public interface MultilineText {
	MultilineText EMPTY = new MultilineText() {
		@Override
		public int drawCenterWithShadow(MatrixStack matrices, int x, int y) {
			return y;
		}

		@Override
		public int drawCenterWithShadow(MatrixStack matrices, int x, int y, int lineHeight, int color) {
			return y;
		}

		@Override
		public int drawWithShadow(MatrixStack matrices, int x, int y, int lineHeight, int color) {
			return y;
		}

		@Override
		public int draw(MatrixStack matrices, int x, int y, int lineHeight, int color) {
			return y;
		}

		@Override
		public int count() {
			return 0;
		}
	};

	static MultilineText create(TextRenderer renderer, StringVisitable text, int width) {
		return create(
			renderer,
			(List<MultilineText.Line>)renderer.wrapLines(text, width)
				.stream()
				.map(orderedText -> new MultilineText.Line(orderedText, renderer.getWidth(orderedText)))
				.collect(ImmutableList.toImmutableList())
		);
	}

	static MultilineText create(TextRenderer renderer, StringVisitable text, int width, int maxLines) {
		return create(
			renderer,
			(List<MultilineText.Line>)renderer.wrapLines(text, width)
				.stream()
				.limit((long)maxLines)
				.map(orderedText -> new MultilineText.Line(orderedText, renderer.getWidth(orderedText)))
				.collect(ImmutableList.toImmutableList())
		);
	}

	static MultilineText create(TextRenderer renderer, Text... texts) {
		return create(
			renderer,
			(List<MultilineText.Line>)Arrays.stream(texts)
				.map(Text::asOrderedText)
				.map(orderedText -> new MultilineText.Line(orderedText, renderer.getWidth(orderedText)))
				.collect(ImmutableList.toImmutableList())
		);
	}

	static MultilineText create(TextRenderer renderer, List<MultilineText.Line> lines) {
		return lines.isEmpty() ? EMPTY : new MultilineText() {
			@Override
			public int drawCenterWithShadow(MatrixStack matrices, int x, int y) {
				return this.drawCenterWithShadow(matrices, x, y, 9, 16777215);
			}

			@Override
			public int drawCenterWithShadow(MatrixStack matrices, int x, int y, int lineHeight, int color) {
				int i = y;

				for (MultilineText.Line line : lines) {
					renderer.drawWithShadow(matrices, line.text, (float)(x - line.width / 2), (float)i, color);
					i += lineHeight;
				}

				return i;
			}

			@Override
			public int drawWithShadow(MatrixStack matrices, int x, int y, int lineHeight, int color) {
				int i = y;

				for (MultilineText.Line line : lines) {
					renderer.drawWithShadow(matrices, line.text, (float)x, (float)i, color);
					i += lineHeight;
				}

				return i;
			}

			@Override
			public int draw(MatrixStack matrices, int x, int y, int lineHeight, int color) {
				int i = y;

				for (MultilineText.Line line : lines) {
					renderer.draw(matrices, line.text, (float)x, (float)i, color);
					i += lineHeight;
				}

				return i;
			}

			@Override
			public int count() {
				return lines.size();
			}
		};
	}

	int drawCenterWithShadow(MatrixStack matrices, int x, int y);

	int drawCenterWithShadow(MatrixStack matrices, int x, int y, int lineHeight, int color);

	int drawWithShadow(MatrixStack matrices, int x, int y, int lineHeight, int color);

	int draw(MatrixStack matrices, int x, int y, int lineHeight, int color);

	int count();

	@Environment(EnvType.CLIENT)
	public static class Line {
		private final OrderedText text;
		private final int width;

		private Line(OrderedText text, int width) {
			this.text = text;
			this.width = width;
		}
	}
}
