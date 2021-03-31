package net.minecraft.client.resource.language;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;

public class TextReorderingProcessor {
	private final String string;
	private final List<Style> styles;
	private final Int2IntFunction reverser;

	private TextReorderingProcessor(String string, List<Style> styles, Int2IntFunction reverser) {
		this.string = string;
		this.styles = ImmutableList.copyOf(styles);
		this.reverser = reverser;
	}

	public String getString() {
		return this.string;
	}

	public List<OrderedText> process(int start, int length, boolean reverse) {
		if (length == 0) {
			return ImmutableList.of();
		} else {
			List<OrderedText> list = Lists.<OrderedText>newArrayList();
			Style style = (Style)this.styles.get(start);
			int i = start;

			for (int j = 1; j < length; j++) {
				int k = start + j;
				Style style2 = (Style)this.styles.get(k);
				if (!style2.equals(style)) {
					String string = this.string.substring(i, k);
					list.add(reverse ? OrderedText.styledStringMapped(string, style, this.reverser) : OrderedText.styledString(string, style));
					style = style2;
					i = k;
				}
			}

			if (i < start + length) {
				String string2 = this.string.substring(i, start + length);
				list.add(reverse ? OrderedText.styledStringMapped(string2, style, this.reverser) : OrderedText.styledString(string2, style));
			}

			return reverse ? Lists.reverse(list) : list;
		}
	}

	public static TextReorderingProcessor method_36144(StringVisitable stringVisitable) {
		return create(stringVisitable, i -> i, string -> string);
	}

	public static TextReorderingProcessor create(StringVisitable visitable, Int2IntFunction reverser, UnaryOperator<String> unaryOperator) {
		StringBuilder stringBuilder = new StringBuilder();
		List<Style> list = Lists.<Style>newArrayList();
		visitable.visit((style, text) -> {
			TextVisitFactory.visitFormatted(text, style, (charIndex, stylex, codePoint) -> {
				stringBuilder.appendCodePoint(codePoint);
				int i = Character.charCount(codePoint);

				for (int j = 0; j < i; j++) {
					list.add(stylex);
				}

				return true;
			});
			return Optional.empty();
		}, Style.EMPTY);
		return new TextReorderingProcessor((String)unaryOperator.apply(stringBuilder.toString()), list, reverser);
	}
}
