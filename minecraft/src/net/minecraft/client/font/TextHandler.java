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
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
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
	public float getWidth(Text text) {
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
	 * @param text the text
	 * @param maxWidth the max width of the trimmed text
	 */
	@Nullable
	public Text trimToWidth(Text text, int maxWidth) {
		TextHandler.WidthLimitingVisitor widthLimitingVisitor = new TextHandler.WidthLimitingVisitor((float)maxWidth);
		return (Text)text.visit(
				(style, string) -> !TextVisitFactory.visitFormatted(string, style, widthLimitingVisitor)
						? Optional.of(new LiteralText(string).setStyle(style))
						: Optional.empty(),
				Style.EMPTY
			)
			.orElse(null);
	}

	public MutableText trimToWidth(Text text, int width, Style style) {
		final TextHandler.WidthLimitingVisitor widthLimitingVisitor = new TextHandler.WidthLimitingVisitor((float)width);
		return (MutableText)text.visit(new Text.StyledVisitor<MutableText>() {
			private final TextCollector collector = new TextCollector();

			@Override
			public Optional<MutableText> accept(Style style, String string) {
				widthLimitingVisitor.resetLength();
				if (!TextVisitFactory.visitFormatted(string, style, widthLimitingVisitor)) {
					String string2 = string.substring(0, widthLimitingVisitor.getLength());
					if (!string2.isEmpty()) {
						this.collector.add(new LiteralText(string2).fillStyle(style));
					}

					return Optional.of(this.collector.getCombined());
				} else {
					if (!string.isEmpty()) {
						this.collector.add(new LiteralText(string).fillStyle(style));
					}

					return Optional.empty();
				}
			}
		}, style).orElseGet(text::shallowCopy);
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

	public List<Text> wrapLines(String text, int maxWidth, Style style) {
		List<Text> list = Lists.<Text>newArrayList();
		this.wrapLines(text, maxWidth, style, false, (stylex, i, j) -> list.add(new LiteralText(text.substring(i, j)).setStyle(stylex)));
		return list;
	}

	public List<Text> wrapLines(Text text, int maxWidth, Style style) {
		List<Text> list = Lists.<Text>newArrayList();
		List<TextHandler.FormattedString> list2 = Lists.<TextHandler.FormattedString>newArrayList();
		text.visit((stylex, string) -> {
			if (!string.isEmpty()) {
				list2.add(new TextHandler.FormattedString(string, stylex));
			}

			return Optional.empty();
		}, style);
		TextHandler.LineWrappingCollector lineWrappingCollector = new TextHandler.LineWrappingCollector(list2);
		boolean bl = true;
		boolean bl2 = false;

		while (bl) {
			bl = false;
			TextHandler.LineBreakingVisitor lineBreakingVisitor = new TextHandler.LineBreakingVisitor((float)maxWidth);

			for (TextHandler.FormattedString formattedString : lineWrappingCollector.parts) {
				boolean bl3 = TextVisitFactory.visitFormatted(formattedString.text, 0, formattedString.style, style, lineBreakingVisitor);
				if (!bl3) {
					int i = lineBreakingVisitor.getEndingIndex();
					Style style2 = lineBreakingVisitor.getEndingStyle();
					char c = lineWrappingCollector.charAt(i);
					boolean bl4 = c == '\n';
					boolean bl5 = bl4 || c == ' ';
					bl2 = bl4;
					list.add(lineWrappingCollector.collectLine(i, bl5 ? 1 : 0, style2));
					bl = true;
					break;
				}

				lineBreakingVisitor.offset(formattedString.text.length());
			}
		}

		Text text2 = lineWrappingCollector.collectRemainers();
		if (text2 != null) {
			list.add(text2);
		} else if (bl2) {
			list.add(new LiteralText("").fillStyle(style));
		}

		return list;
	}

	@Environment(EnvType.CLIENT)
	static class FormattedString {
		private final String text;
		private final Style style;

		public FormattedString(String text, Style style) {
			this.text = text;
			this.style = style;
		}

		public MutableText getText() {
			return new LiteralText(this.text).setStyle(this.style);
		}
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
		private final List<TextHandler.FormattedString> parts;
		private String joined;

		public LineWrappingCollector(List<TextHandler.FormattedString> parts) {
			this.parts = parts;
			this.joined = (String)parts.stream().map(formattedString -> formattedString.text).collect(Collectors.joining());
		}

		public char charAt(int index) {
			return this.joined.charAt(index);
		}

		public Text collectLine(int lineLength, int skippedLength, Style style) {
			TextCollector textCollector = new TextCollector();
			ListIterator<TextHandler.FormattedString> listIterator = this.parts.listIterator();
			int i = lineLength;
			boolean bl = false;

			while (listIterator.hasNext()) {
				TextHandler.FormattedString formattedString = (TextHandler.FormattedString)listIterator.next();
				String string = formattedString.text;
				int j = string.length();
				if (!bl) {
					if (i > j) {
						textCollector.add(formattedString.getText());
						listIterator.remove();
						i -= j;
					} else {
						String string2 = string.substring(0, i);
						if (!string2.isEmpty()) {
							textCollector.add(new LiteralText(string2).setStyle(formattedString.style));
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
							listIterator.set(new TextHandler.FormattedString(string2, style));
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
		public Text collectRemainers() {
			TextCollector textCollector = new TextCollector();
			this.parts.forEach(formattedString -> textCollector.add(formattedString.getText()));
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
