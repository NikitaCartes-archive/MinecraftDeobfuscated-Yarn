package net.minecraft.text;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.CharacterVisitor;
import net.minecraft.client.font.TextVisitFactory;

/**
 * An object that can supply character code points
 * to a visitor, with a style context.
 */
@FunctionalInterface
public interface OrderedText {
	/**
	 * An empty text that does not call the visitors.
	 */
	OrderedText EMPTY = characterVisitor -> true;

	@Environment(EnvType.CLIENT)
	boolean accept(CharacterVisitor visitor);

	@Environment(EnvType.CLIENT)
	static OrderedText styled(int codePoint, Style style) {
		return visitor -> visitor.accept(0, style, codePoint);
	}

	@Environment(EnvType.CLIENT)
	static OrderedText styledString(String string, Style style) {
		return string.isEmpty() ? EMPTY : visitor -> TextVisitFactory.visitForwards(string, style, visitor);
	}

	@Environment(EnvType.CLIENT)
	static OrderedText styledStringMapped(String string, Style style, Int2IntFunction codePointMapper) {
		return string.isEmpty() ? EMPTY : visitor -> TextVisitFactory.visitBackwards(string, style, map(visitor, codePointMapper));
	}

	@Environment(EnvType.CLIENT)
	static CharacterVisitor map(CharacterVisitor visitor, Int2IntFunction codePointMapper) {
		return (charIndex, style, charPoint) -> visitor.accept(charIndex, style, codePointMapper.apply(Integer.valueOf(charPoint)));
	}

	@Environment(EnvType.CLIENT)
	static OrderedText concat(OrderedText first, OrderedText second) {
		return innerConcat(first, second);
	}

	@Environment(EnvType.CLIENT)
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

	@Environment(EnvType.CLIENT)
	static OrderedText innerConcat(OrderedText text1, OrderedText text2) {
		return visitor -> text1.accept(visitor) && text2.accept(visitor);
	}

	@Environment(EnvType.CLIENT)
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
