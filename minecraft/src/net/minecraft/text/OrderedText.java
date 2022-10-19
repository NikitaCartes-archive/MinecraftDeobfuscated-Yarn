package net.minecraft.text;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.List;

/**
 * An object that can supply character code points
 * to a visitor, with a style context.
 */
@FunctionalInterface
public interface OrderedText {
	/**
	 * An empty text that does not call the visitors.
	 */
	OrderedText EMPTY = visitor -> true;

	boolean accept(CharacterVisitor visitor);

	static OrderedText styled(int codePoint, Style style) {
		return visitor -> visitor.accept(0, style, codePoint);
	}

	static OrderedText styledForwardsVisitedString(String string, Style style) {
		return string.isEmpty() ? EMPTY : visitor -> TextVisitFactory.visitForwards(string, style, visitor);
	}

	static OrderedText styledForwardsVisitedString(String string, Style style, Int2IntFunction codePointMapper) {
		return string.isEmpty() ? EMPTY : visitor -> TextVisitFactory.visitForwards(string, style, map(visitor, codePointMapper));
	}

	static OrderedText styledBackwardsVisitedString(String string, Style style) {
		return string.isEmpty() ? EMPTY : visitor -> TextVisitFactory.visitBackwards(string, style, visitor);
	}

	static OrderedText styledBackwardsVisitedString(String string, Style style, Int2IntFunction codePointMapper) {
		return string.isEmpty() ? EMPTY : visitor -> TextVisitFactory.visitBackwards(string, style, map(visitor, codePointMapper));
	}

	static CharacterVisitor map(CharacterVisitor visitor, Int2IntFunction codePointMapper) {
		return (charIndex, style, charPoint) -> visitor.accept(charIndex, style, codePointMapper.apply(Integer.valueOf(charPoint)));
	}

	static OrderedText empty() {
		return EMPTY;
	}

	static OrderedText of(OrderedText text) {
		return text;
	}

	static OrderedText concat(OrderedText first, OrderedText second) {
		return innerConcat(first, second);
	}

	static OrderedText concat(OrderedText... texts) {
		return innerConcat(ImmutableList.copyOf(texts));
	}

	static OrderedText concat(List<OrderedText> texts) {
		int i = texts.size();
		switch (i) {
			case 0:
				return EMPTY;
			case 1:
				return (OrderedText)texts.get(0);
			case 2:
				return innerConcat((OrderedText)texts.get(0), (OrderedText)texts.get(1));
			default:
				return innerConcat(ImmutableList.copyOf(texts));
		}
	}

	static OrderedText innerConcat(OrderedText text1, OrderedText text2) {
		return visitor -> text1.accept(visitor) && text2.accept(visitor);
	}

	static OrderedText innerConcat(List<OrderedText> texts) {
		return visitor -> {
			for (OrderedText orderedText : texts) {
				if (!orderedText.accept(visitor)) {
					return false;
				}
			}

			return true;
		};
	}
}
