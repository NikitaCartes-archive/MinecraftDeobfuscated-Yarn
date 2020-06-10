package net.minecraft.client.font;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.TextCollector;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Style;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

@Environment(EnvType.CLIENT)
public class TextHandler {
	private final TextHandler.WidthRetriever widthRetriever;

	public TextHandler(TextHandler.WidthRetriever widthRetriever) {
		this.widthRetriever = widthRetriever;
	}

	/**
	 * Returns the width of a string.
	 */
	public float getWidth(@Nullable String text) {
		if (text == null) {
			return 0.0F;
		} else {
			MutableFloat mutableFloat = new MutableFloat();
			TextVisitFactory.visitFormatted(text, Style.EMPTY, (i, style, j) -> {
				mutableFloat.add(this.widthRetriever.getWidth(j, style));
				return true;
			});
			return mutableFloat.floatValue();
		}
	}

	/**
	 * Returns the width of a text.
	 */
	public float getWidth(StringRenderable text) {
		MutableFloat mutableFloat = new MutableFloat();
		TextVisitFactory.visitFormatted(text, Style.EMPTY, (i, style, j) -> {
			mutableFloat.add(this.widthRetriever.getWidth(j, style));
			return true;
		});
		return mutableFloat.floatValue();
	}

	/**
	 * Returns the length of a string when it is trimmed to be at most {@code
	 * maxWidth} wide.
	 * 
	 * @param text the string to trim
	 * @param maxWidth the max width of the trimmed string
	 * @param style the style of the trimmed string
	 */
	public int getTrimmedLength(String text, int maxWidth, Style style) {
		TextHandler.WidthLimitingVisitor widthLimitingVisitor = new TextHandler.WidthLimitingVisitor((float)maxWidth);
		TextVisitFactory.visitForwards(text, style, widthLimitingVisitor);
		return widthLimitingVisitor.getLength();
	}

	/**
	 * Trim a string to be at most {@code maxWidth} wide.
	 * 
	 * @return the trimmed string
	 * 
	 * @param text the string to trim
	 * @param maxWidth the max width of the trimmed string
	 * @param style the style of the trimmed string
	 */
	public String trimToWidth(String text, int maxWidth, Style style) {
		return text.substring(0, this.getTrimmedLength(text, maxWidth, style));
	}

	/**
	 * Trim a string from right to left to be at most {@code maxWidth} wide.
	 * 
	 * @return the trimmed string
	 * 
	 * @param text the string to trim
	 * @param maxWidth the max width of the trimmed string
	 * @param style the style of the trimmed string
	 */
	public String trimToWidthBackwards(String text, int maxWidth, Style style) {
		MutableFloat mutableFloat = new MutableFloat();
		MutableInt mutableInt = new MutableInt(text.length());
		TextVisitFactory.visitBackwards(text, style, (j, stylex, k) -> {
			float f = mutableFloat.addAndGet(this.widthRetriever.getWidth(k, stylex));
			if (f > (float)maxWidth) {
				return false;
			} else {
				mutableInt.setValue(j);
				return true;
			}
		});
		return text.substring(mutableInt.intValue());
	}

	/**
	 * Trim a text to be at most {@code maxWidth} wide.
	 * 
	 * @return the trimmed text
	 * 
	 * @param maxWidth the max width of the trimmed text
	 */
	@Nullable
	public Style trimToWidth(StringRenderable text, int maxWidth) {
		TextHandler.WidthLimitingVisitor widthLimitingVisitor = new TextHandler.WidthLimitingVisitor((float)maxWidth);
		return (Style)text.visit(
				(style, string) -> TextVisitFactory.visitFormatted(string, style, widthLimitingVisitor) ? Optional.empty() : Optional.of(style), Style.EMPTY
			)
			.orElse(null);
	}

	public StringRenderable trimToWidth(StringRenderable text, int width, Style style) {
		final TextHandler.WidthLimitingVisitor widthLimitingVisitor = new TextHandler.WidthLimitingVisitor((float)width);
		return (StringRenderable)text.visit(new StringRenderable.StyledVisitor<StringRenderable>() {
			private final TextCollector collector = new TextCollector();

			@Override
			public Optional<StringRenderable> accept(Style style, String string) {
				widthLimitingVisitor.resetLength();
				if (!TextVisitFactory.visitFormatted(string, style, widthLimitingVisitor)) {
					String string2 = string.substring(0, widthLimitingVisitor.getLength());
					if (!string2.isEmpty()) {
						this.collector.add(StringRenderable.styled(string2, style));
					}

					return Optional.of(this.collector.getCombined());
				} else {
					if (!string.isEmpty()) {
						this.collector.add(StringRenderable.styled(string, style));
					}

					return Optional.empty();
				}
			}
		}, style).orElse(text);
	}

	public static int moveCursorByWords(String text, int offset, int cursor, boolean consumeSpaceOrBreak) {
		int i = cursor;
		boolean bl = offset < 0;
		int j = Math.abs(offset);

		for (int k = 0; k < j; k++) {
			if (bl) {
				while (consumeSpaceOrBreak && i > 0 && (text.charAt(i - 1) == ' ' || text.charAt(i - 1) == '\n')) {
					i--;
				}

				while (i > 0 && text.charAt(i - 1) != ' ' && text.charAt(i - 1) != '\n') {
					i--;
				}
			} else {
				int l = text.length();
				int m = text.indexOf(32, i);
				int n = text.indexOf(10, i);
				if (m == -1 && n == -1) {
					i = -1;
				} else if (m != -1 && n != -1) {
					i = Math.min(m, n);
				} else if (m != -1) {
					i = m;
				} else {
					i = n;
				}

				if (i == -1) {
					i = l;
				} else {
					while (consumeSpaceOrBreak && i < l && (text.charAt(i) == ' ' || text.charAt(i) == '\n')) {
						i++;
					}
				}
			}
		}

		return i;
	}

	public void wrapLines(String text, int maxWidth, Style style, boolean retainTrailingWordSplit, TextHandler.LineWrappingConsumer consumer) {
		int i = 0;
		int j = text.length();
		Style style2 = style;

		while (i < j) {
			TextHandler.LineBreakingVisitor lineBreakingVisitor = new TextHandler.LineBreakingVisitor((float)maxWidth);
			boolean bl = TextVisitFactory.visitFormatted(text, i, style2, style, lineBreakingVisitor);
			if (bl) {
				consumer.accept(style2, i, j);
				break;
			}

			int k = lineBreakingVisitor.getEndingIndex();
			char c = text.charAt(k);
			int l = c != '\n' && c != ' ' ? k : k + 1;
			consumer.accept(style2, i, retainTrailingWordSplit ? l : k);
			i = l;
			style2 = lineBreakingVisitor.getEndingStyle();
		}
	}

	public List<StringRenderable> wrapLines(String text, int maxWidth, Style style) {
		List<StringRenderable> list = Lists.<StringRenderable>newArrayList();
		this.wrapLines(text, maxWidth, style, false, (stylex, i, j) -> list.add(StringRenderable.styled(text.substring(i, j), stylex)));
		return list;
	}

	public List<StringRenderable> wrapLines(StringRenderable stringRenderable, int maxWidth, Style style) {
		return this.method_29971(stringRenderable, maxWidth, style, null);
	}

	public List<StringRenderable> method_29971(StringRenderable stringRenderable, int i, Style style, @Nullable StringRenderable stringRenderable2) {
		List<StringRenderable> list = Lists.<StringRenderable>newArrayList();
		List<TextHandler.StyledString> list2 = Lists.<TextHandler.StyledString>newArrayList();
		stringRenderable.visit((stylex, string) -> {
			if (!string.isEmpty()) {
				list2.add(new TextHandler.StyledString(string, stylex));
			}

			return Optional.empty();
		}, style);
		TextHandler.LineWrappingCollector lineWrappingCollector = new TextHandler.LineWrappingCollector(list2);
		boolean bl = true;
		boolean bl2 = false;
		boolean bl3 = false;

		while (bl) {
			bl = false;
			TextHandler.LineBreakingVisitor lineBreakingVisitor = new TextHandler.LineBreakingVisitor((float)i);

			for (TextHandler.StyledString styledString : lineWrappingCollector.parts) {
				boolean bl4 = TextVisitFactory.visitFormatted(styledString.literal, 0, styledString.style, style, lineBreakingVisitor);
				if (!bl4) {
					int j = lineBreakingVisitor.getEndingIndex();
					Style style2 = lineBreakingVisitor.getEndingStyle();
					char c = lineWrappingCollector.charAt(j);
					boolean bl5 = c == '\n';
					boolean bl6 = bl5 || c == ' ';
					bl2 = bl5;
					StringRenderable stringRenderable3 = lineWrappingCollector.collectLine(j, bl6 ? 1 : 0, style2);
					list.add(this.method_29972(stringRenderable3, bl3, stringRenderable2));
					bl3 = !bl5;
					bl = true;
					break;
				}

				lineBreakingVisitor.offset(styledString.literal.length());
			}
		}

		StringRenderable stringRenderable4 = lineWrappingCollector.collectRemainers();
		if (stringRenderable4 != null) {
			list.add(this.method_29972(stringRenderable4, bl3, stringRenderable2));
		} else if (bl2) {
			list.add(StringRenderable.EMPTY);
		}

		return list;
	}

	private StringRenderable method_29972(StringRenderable stringRenderable, boolean bl, StringRenderable stringRenderable2) {
		return bl && stringRenderable2 != null ? StringRenderable.concat(stringRenderable2, stringRenderable) : stringRenderable;
	}

	@Environment(EnvType.CLIENT)
	class LineBreakingVisitor implements TextVisitFactory.CharacterVisitor {
		private final float maxWidth;
		private int endIndex = -1;
		private Style endStyle = Style.EMPTY;
		private boolean nonEmpty;
		private float totalWidth;
		private int lastSpaceBreak = -1;
		private Style lastSpaceStyle = Style.EMPTY;
		private int count;
		private int startOffset;

		public LineBreakingVisitor(float maxWidth) {
			this.maxWidth = Math.max(maxWidth, 1.0F);
		}

		@Override
		public boolean onChar(int i, Style style, int j) {
			int k = i + this.startOffset;
			switch (j) {
				case 10:
					return this.breakLine(k, style);
				case 32:
					this.lastSpaceBreak = k;
					this.lastSpaceStyle = style;
				default:
					float f = TextHandler.this.widthRetriever.getWidth(j, style);
					this.totalWidth += f;
					if (!this.nonEmpty || !(this.totalWidth > this.maxWidth)) {
						this.nonEmpty |= f != 0.0F;
						this.count = k + Character.charCount(j);
						return true;
					} else {
						return this.lastSpaceBreak != -1 ? this.breakLine(this.lastSpaceBreak, this.lastSpaceStyle) : this.breakLine(k, style);
					}
			}
		}

		private boolean breakLine(int finishIndex, Style finishStyle) {
			this.endIndex = finishIndex;
			this.endStyle = finishStyle;
			return false;
		}

		private boolean hasLineBreak() {
			return this.endIndex != -1;
		}

		public int getEndingIndex() {
			return this.hasLineBreak() ? this.endIndex : this.count;
		}

		public Style getEndingStyle() {
			return this.endStyle;
		}

		public void offset(int extraOffset) {
			this.startOffset += extraOffset;
		}
	}

	@Environment(EnvType.CLIENT)
	static class LineWrappingCollector {
		private final List<TextHandler.StyledString> parts;
		private String joined;

		public LineWrappingCollector(List<TextHandler.StyledString> parts) {
			this.parts = parts;
			this.joined = (String)parts.stream().map(styledString -> styledString.literal).collect(Collectors.joining());
		}

		public char charAt(int index) {
			return this.joined.charAt(index);
		}

		public StringRenderable collectLine(int lineLength, int skippedLength, Style style) {
			TextCollector textCollector = new TextCollector();
			ListIterator<TextHandler.StyledString> listIterator = this.parts.listIterator();
			int i = lineLength;
			boolean bl = false;

			while (listIterator.hasNext()) {
				TextHandler.StyledString styledString = (TextHandler.StyledString)listIterator.next();
				String string = styledString.literal;
				int j = string.length();
				if (!bl) {
					if (i > j) {
						textCollector.add(styledString);
						listIterator.remove();
						i -= j;
					} else {
						String string2 = string.substring(0, i);
						if (!string2.isEmpty()) {
							textCollector.add(StringRenderable.styled(string2, styledString.style));
						}

						i += skippedLength;
						bl = true;
					}
				}

				if (bl) {
					if (i <= j) {
						String string2 = string.substring(i);
						if (string2.isEmpty()) {
							listIterator.remove();
						} else {
							listIterator.set(new TextHandler.StyledString(string2, style));
						}
						break;
					}

					listIterator.remove();
					i -= j;
				}
			}

			this.joined = this.joined.substring(lineLength + skippedLength);
			return textCollector.getCombined();
		}

		@Nullable
		public StringRenderable collectRemainers() {
			TextCollector textCollector = new TextCollector();
			this.parts.forEach(textCollector::add);
			this.parts.clear();
			return textCollector.getRawCombined();
		}
	}

	/**
	 * A consumer of the line-breaking information.
	 */
	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface LineWrappingConsumer {
		/**
		 * Accepts the information on a line-wrap segment.
		 * 
		 * @param style the style of this line segment
		 * @param start the start index of this segment in the original string, inclusive
		 * @param end the end index of this segment in the original string, exclusive
		 */
		void accept(Style style, int start, int end);
	}

	@Environment(EnvType.CLIENT)
	static class StyledString implements StringRenderable {
		private final String literal;
		private final Style style;

		public StyledString(String literal, Style style) {
			this.literal = literal;
			this.style = style;
		}

		@Override
		public <T> Optional<T> visit(StringRenderable.Visitor<T> visitor) {
			return visitor.accept(this.literal);
		}

		@Override
		public <T> Optional<T> visit(StringRenderable.StyledVisitor<T> styledVisitor, Style style) {
			return styledVisitor.accept(this.style.withParent(style), this.literal);
		}
	}

	@Environment(EnvType.CLIENT)
	class WidthLimitingVisitor implements TextVisitFactory.CharacterVisitor {
		private float widthLeft;
		private int length;

		public WidthLimitingVisitor(float maxWidth) {
			this.widthLeft = maxWidth;
		}

		@Override
		public boolean onChar(int i, Style style, int j) {
			this.widthLeft = this.widthLeft - TextHandler.this.widthRetriever.getWidth(j, style);
			if (this.widthLeft >= 0.0F) {
				this.length = i + Character.charCount(j);
				return true;
			} else {
				return false;
			}
		}

		public int getLength() {
			return this.length;
		}

		public void resetLength() {
			this.length = 0;
		}
	}

	/**
	 * A retriever of character width.
	 */
	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface WidthRetriever {
		/**
		 * Returns the width of a character.
		 * 
		 * @param codePoint the character's code point
		 * @param style the style of the character, carrying font and formatting information
		 */
		float getWidth(int codePoint, Style style);
	}
}
